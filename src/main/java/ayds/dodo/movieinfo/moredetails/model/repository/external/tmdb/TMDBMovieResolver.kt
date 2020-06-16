package ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb

import ayds.dodo.movieinfo.moredetails.model.entities.DefaultMovie
import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException

internal interface TMDBMovieResolver {

    fun getMovie(body: String?, year: String): TMDBMovie

}

internal class TMDBMovieResolverImp :TMDBMovieResolver {

    private val titleProperty = "title"
    private val overviewProperty = "overview"
    private val backdropPathProperty = "backdrop_path"
    private val posterPathProperty = "poster_path"
    private val releaseDateProperty = "release_date"
    private val pathUrl = "https://image.tmdb.org/t/p/w400/"

    override fun getMovie(body: String?,year: String): TMDBMovie {
        val searchResult = searchMovie(body, year)
        val movieData = TMDBMovie()

        searchResult?.let {
            val extract = searchResult[overviewProperty]
            if (isNotNull(extract)) {
                movieData.title = getTitleFromJson(searchResult[titleProperty])
                movieData.plot = extract.asString
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
    private fun isNotNull(element: JsonElement?): Boolean =
            element != null && !element.isJsonNull

    private fun getTitleFromJson(titleJson: JsonElement) =
            if (isNotNull(titleJson)) titleJson.asString else ""

    private fun getImageUrlFromJson(backdropPathJson: JsonElement) =
            if (isNotNull(backdropPathJson)) pathUrl + backdropPathJson.asString else ""

    private fun getPosterPathFromJSon(posterPathJSon: JsonElement) =
            if (isNotNull(posterPathJSon)) pathUrl + posterPathJSon.asString else ""
}