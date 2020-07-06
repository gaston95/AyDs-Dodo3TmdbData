package ayds.dodo3.tmdb.tmdb


import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException

internal interface TMDBMovieResolver {

    fun getMovie(body: String?, year: String): TMDBMovieResponse

}

internal class TMDBMovieResolverImp : TMDBMovieResolver {

    private val titleProperty = "title"
    private val overviewProperty = "overview"
    private val backdropPathProperty = "backdrop_path"
    private val posterPathProperty = "poster_path"
    private val releaseDateProperty = "release_date"
    private val pathUrl = "https://image.tmdb.org/t/p/w400/"
    private var defaultImageUrl = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"

    override fun getMovie(body: String?,year: String): TMDBMovieResponse {
        val searchResult = searchMovie(body, year)
        val movieData = TMDBMovieResponse()

        searchResult?.let {
            val extract = searchResult[overviewProperty]
            extract.getOrNull()?.let {
                movieData.title = getTitleFromJson(searchResult[titleProperty])
                movieData.plot = it.asString
                movieData.imageUrl = getImageUrlFromJson(searchResult[backdropPathProperty])
                movieData.posterPath = getPosterPathFromJSon(searchResult[posterPathProperty])

                return movieData
            }
        }
        return DefaultMovie
    }

    private fun searchMovie(body: String?,year: String): JsonObject? {
        try {
            val jobj = Gson().fromJson(body, JsonObject::class.java)
            val resultIterator = jobj["results"].asJsonArray.iterator()
            var result: JsonObject
            while (resultIterator.hasNext()) {
                result = resultIterator.next().asJsonObject
                if (areSameYear(result,year)) return result
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun areSameYear(result: JsonObject, movieYear: String): Boolean {
        val yearJson = result[releaseDateProperty]
        val year = yearJson?.asString?.split("-")?.toTypedArray()?.get(0) ?: ""
        return year == movieYear
    }

    private fun JsonElement?.getOrNull(): JsonElement? =
        if (this != null && !isJsonNull) this
        else null

    private fun getTitleFromJson(titleJson: JsonElement) =
        titleJson.getOrNull()?.asString ?: ""

    private fun getImageUrlFromJson(backdropPathJson: JsonElement) =
        backdropPathJson.getOrNull()?.let { pathUrl + it.asString } ?: defaultImageUrl

    private fun getPosterPathFromJSon(posterPathJSon: JsonElement) =
        posterPathJSon.getOrNull()?.let { pathUrl + it.asString }
}