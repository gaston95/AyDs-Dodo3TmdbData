package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class OtherInfoData(val movie: OmdbMovie) {
    private val overviewProperty = "overview"
    private val backdropPathProperty = "backdrop_path"
    private val posterPathProperty = "poster_path"
    private val imageUrlDefault = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
    private val pathUrl = "https://image.tmdb.org/t/p/w400/"
    private val apiUrl = "https://api.themoviedb.org/3/"
    private val localMovie = "[*]"
    private val noResults = "No results"
    private var title = ""
    private var imageUrl:String = imageUrlDefault
    private var text:String = noResults
    private var posterPath:String = ""

    init {
        buildMovieInfo()
    }

    fun getTitle() = title

    fun getText() = text

    fun getImageURL() = imageUrl

    fun getPosterPath() = posterPath

    private fun buildMovieInfo() {
        DataBase.createNewDatabase()
        title = movie.title
        val movieText = DataBase.getOverview(movie.title)
        val movieImageUrl = DataBase.getImageUrl(movie.title)

        if (movieExistsInDb(movieText, movieImageUrl)) {
            text = getTextInDB(movieText)
            imageUrl = movieImageUrl!!
        }
        else buildMovieInfoFromAPI()
    }

    private fun buildMovieInfoFromAPI(){
        val searchResult = searchMovie(movie)

        if (searchResult != null) {
            val extract = searchResult[overviewProperty]

            if (isNotNull(extract)) {
                text = extract.asString

                setImageUrlFromJson(searchResult[backdropPathProperty])
                setPosterPathFromJSon(searchResult[posterPathProperty])

                DataBase.saveMovieInfo(movie.title, text, imageUrl)
            }
        }
    }

    private fun setImageUrlFromJson(backdropPathJson: JsonElement){
        if (isNotNull(backdropPathJson))
            imageUrl = pathUrl + backdropPathJson.asString
    }

    private fun setPosterPathFromJSon(posterPathJSon: JsonElement) {
        if (isNotNull(posterPathJSon))
            posterPath = pathUrl + posterPathJSon.asString
    }

    private fun movieExistsInDb(text: String?, path: String?): Boolean = text != null && path != null

    private fun getTextInDB(text: String?): String = localMovie + text

    private fun isNotNull(element: JsonElement?): Boolean = element != null && !element.isJsonNull

    private fun searchMovie(movie: OmdbMovie): JsonObject? {
        val tmdbAPI = createAPI()
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