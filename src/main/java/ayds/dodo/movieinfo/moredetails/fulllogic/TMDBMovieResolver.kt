package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class TMDBMovieResolver(val movie: OmdbMovie) {
    private val overviewProperty = "overview"
    private val backdropPathProperty = "backdrop_path"
    private val posterPathProperty = "poster_path"
    private val imageUrlDefault = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
    private val pathUrl = "https://image.tmdb.org/t/p/w400/"
    private val apiUrl = "https://api.themoviedb.org/3/"
    private val localMovie = "[*]"
    private val noResults = "No results"
    private val tmdbAPI : TheMovieDBAPI

    init {
        tmdbAPI = createAPI()
    }

    fun getMovie(): TMDBMovie {
        val movieData = getMovieFromDataBase(movie.title)
        return movieData?.let { setMovieTextAsLocallyStored(it) } ?: buildMovieInfo()
    }

    private fun buildMovieInfo(): TMDBMovie {
        val movieData = buildMovieInfoFromAPI()
        if(movieData.title!=noResults)
            DataBase.saveMovieInfo(movieData)
        return movieData
    }

    private fun buildMovieInfoFromAPI(): TMDBMovie {
        val searchResult = searchMovie(movie)
        val movieData = TMDBMovie()
        setMovieAsDefault(movieData)

        searchResult?.let {
            val extract = searchResult[overviewProperty]

            if (isNotNull(extract)) {
                movieData.title = movie.title
                movieData.plot = extract.asString
                movieData.imageUrl = getImageUrlFromJson(searchResult[backdropPathProperty])

                val posterPath = getPosterPathFromJSon(searchResult[posterPathProperty])
                movieData.plot = HTMLFormatter.getFormattedPlotText(movieData, posterPath)
            }
        }
        return movieData
    }

    private fun setMovieAsDefault(movieData:TMDBMovie){
        movieData.title = noResults
        movieData.imageUrl = imageUrlDefault
        movieData.plot = noResults
    }

    private fun getImageUrlFromJson(backdropPathJson: JsonElement) =
            if (isNotNull(backdropPathJson)) pathUrl + backdropPathJson.asString else imageUrlDefault

    private fun getPosterPathFromJSon(posterPathJSon: JsonElement) =
            if (isNotNull(posterPathJSon)) pathUrl + posterPathJSon.asString else ""

    private fun getMovieFromDataBase(titulo: String): TMDBMovie? {
        DataBase.createNewDatabase()
        return DataBase.getMovieInfo(titulo)
    }

    private fun setMovieTextAsLocallyStored(movie: TMDBMovie): TMDBMovie {
        movie.plot = localMovie + movie.plot
        return movie
    }

    private fun isNotNull(element: JsonElement?): Boolean = element != null && !element.isJsonNull

    private fun searchMovie(movie: OmdbMovie): JsonObject? {
        try {
            val callResponse = tmdbAPI.getTerm(movie.title)?.execute()
            val jobj = Gson().fromJson(callResponse?.body(), JsonObject::class.java)
            val resultIterator = jobj["results"].asJsonArray.iterator()
            var result: JsonObject
            while (resultIterator.hasNext()) {
                result = resultIterator.next().asJsonObject
                if (areSameYear(result, movie.year)) return result
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createAPI(): TheMovieDBAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        return retrofit.create(TheMovieDBAPI::class.java)
    }

    private fun areSameYear(result: JsonObject, movieYear: String): Boolean {
        val yearJson = result["release_date"]
        val year = yearJson?.asString?.split("-")?.toTypedArray()?.get(0) ?: ""
        return year == movieYear
    }
}