package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class OtherInfoData(movie: OmdbMovie) {

    private val image_url_default = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
    private val path_url = "https://image.tmdb.org/t/p/w400/"
    private val api_url = "https://api.themoviedb.org/3/"
    private val local_movie = "[*]"
    private val no_results = "No results"
    private var imageUrl:String = ""
    private var text:String = ""
    private var posterPath:String = ""

    init {
        buildMovieInfo(movie)
    }

    fun getText() = text

    fun getImageURL() = imageUrl

    fun getPosterPath() = posterPath

    private fun buildMovieInfo(movie: OmdbMovie) {
            DataBase.createNewDatabase()
            val movieText = DataBase.getOverview(movie.title)
            val movieImageUrl = DataBase.getImageUrl(movie.title)

            if (movieExistsInDb(movieText, movieImageUrl)) {
                text = getTextInDB(movieText)
            } else {
                text = no_results
                imageUrl = image_url_default

                val searchResult = searchMovie(movie)

                if (searchResult != null) {

                    val extract = searchResult["overview"]

                    if (isNotNull(extract)) {

                        text = extract.asString

                        val backdropPathJson = searchResult["backdrop_path"]
                        if (isNotNull(backdropPathJson))
                            imageUrl = path_url + backdropPathJson.asString

                        val posterPathJSon = searchResult["poster_path"]
                        if (isNotNull(posterPathJSon))
                            posterPath = path_url + posterPathJSon.asString

                        DataBase.saveMovieInfo(movie.title, text, imageUrl)
                    }
                }
            }
        }

    private fun movieExistsInDb(text: String?, path: String?): Boolean = text != null && path != null

    private fun getTextInDB(text: String?): String = local_movie + text

    private fun isNotNull(element: JsonElement?): Boolean = element != null && !element.isJsonNull

    private fun searchMovie(movie: OmdbMovie): JsonObject? {
        val tmdbAPI = createAPI()
        try {
            val callResponse = tmdbAPI.getTerm(movie.title)?.execute()
            val gson = Gson()
            val jobj = gson.fromJson(callResponse?.body(), JsonObject::class.java)
            val resultIterator: Iterator<JsonElement> = jobj["results"].asJsonArray.iterator()
            var result: JsonObject
            while (resultIterator.hasNext()) {
                result = resultIterator.next().asJsonObject
                if (areSameYear(result, movie.year)) {
                    return result
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createAPI(): TheMovieDBAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(api_url)
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