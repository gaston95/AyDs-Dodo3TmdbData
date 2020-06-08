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
    private var movieData : TMDBMovie? = null

    init {
        tmdbAPI = createAPI()
        buildMovieInfo()
    }

    private fun buildMovieInfo() {
        DataBase.createNewDatabase()
        movieData = DataBase.getMovieInfo(movie.title)
        if(movieData!=null) movieData!!.plot = markTextAsLocallyStored(movieData!!.plot)
        else buildMovieInfoFromAPI()
    }

    fun getMovie() = movieData

    private fun buildMovieInfoFromAPI(){
        val searchResult = searchMovie(movie)

        searchResult?.let {
            val extract = searchResult[overviewProperty]
            movieData = TMDBMovie()
            if (isNotNull(extract)) {
                movieData!!.title = movie.title
                movieData!!.plot = extract.asString
                movieData!!.imageUrl = getImageUrlFromJson(searchResult[backdropPathProperty])

                val posterPath = getPosterPathFromJSon(searchResult[posterPathProperty])
                movieData!!.plot = HTMLFormatter.getFormattedPlotText(movieData!!, posterPath)

                DataBase.saveMovieInfo(movieData!!)
            }
        }
    }

    private fun getImageUrlFromJson(backdropPathJson: JsonElement) =
            if (isNotNull(backdropPathJson)) pathUrl + backdropPathJson.asString else imageUrlDefault


    private fun getPosterPathFromJSon(posterPathJSon: JsonElement) =
            if (isNotNull(posterPathJSon)) pathUrl + posterPathJSon.asString else ""


    private fun movieExistsInDb(text: String?, path: String?): Boolean = text != null && path != null

    private fun markTextAsLocallyStored(text: String?): String = localMovie + text

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