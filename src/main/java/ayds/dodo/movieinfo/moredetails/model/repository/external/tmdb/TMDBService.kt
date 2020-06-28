package ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb


import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.external.ExternalService
import retrofit2.Response

internal class TMDBService (private val tmdbAPI: TheMovieDBAPI,
                            private val tmdbMovieResolver: TMDBMovieResolver
) : ExternalService {

    override fun getMovie(title: String, year: String): TMDBMovie {
        val callResponse = getTMDBMovieFromService(title)
        return tmdbMovieResolver.getMovie(callResponse.body(),year)
    }

    private fun getTMDBMovieFromService(title: String): Response<String> {
        return tmdbAPI.getTerm(title).execute()
    }

}
