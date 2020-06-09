package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.external.tmdb


import ayds.dodo.movieinfo.moredetails.fulllogic.HTMLFormatter
import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.DefaultMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.external.ExternalService
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException

class TMDBService (private val TMDBAPI: TheMovieDBAPI,
                   private val TMDBMovieResolver: TMDBMovieResolver
) : ExternalService {

    private val overviewProperty = "overview"
    private val backdropPathProperty = "backdrop_path"
    private val posterPathProperty = "poster_path"
    private val imageUrlDefault = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
    private val pathUrl = "https://image.tmdb.org/t/p/w400/"
    private val apiUrl = "https://api.themoviedb.org/3/"
    
    override fun getMovie(title: String,year: String): TMDBMovie {
        val searchResult = searchMovie(title, year)
        val movieData = TMDBMovie()

        searchResult?.let {
            val extract = searchResult[overviewProperty]
            if (isNotNull(extract)) {
                movieData.title = title
                movieData.plot = extract.asString
                movieData.imageUrl = getImageUrlFromJson(searchResult[backdropPathProperty])

                val posterPath = getPosterPathFromJSon(searchResult[posterPathProperty])
                movieData.plot =
                        HTMLFormatter.getFormattedPlotText(
                                movieData,
                                posterPath
                        )
                return movieData
            }
        }
        return DefaultMovie
    }

    private fun searchMovie(title: String,year: String): JsonObject? {
        try {
            val callResponse = TMDBAPI.getTerm(title)?.execute()
            val jobj = Gson().fromJson(callResponse?.body(), JsonObject::class.java)
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
        val yearJson = result["release_date"]
        val year = yearJson?.asString?.split("-")?.toTypedArray()?.get(0) ?: ""
        return year == movieYear
    }
    private fun isNotNull(element: JsonElement?): Boolean = element != null && !element.isJsonNull

    private fun getImageUrlFromJson(backdropPathJson: JsonElement) =
            if (isNotNull(backdropPathJson)) pathUrl + backdropPathJson.asString else imageUrlDefault

    private fun getPosterPathFromJSon(posterPathJSon: JsonElement) =
            if (isNotNull(posterPathJSon)) pathUrl + posterPathJSon.asString else ""
}
