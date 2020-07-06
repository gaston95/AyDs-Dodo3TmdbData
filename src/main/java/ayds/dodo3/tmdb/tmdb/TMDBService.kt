package ayds.dodo3.tmdb.tmdb



import retrofit2.Response

internal class TMDBService (private val tmdbAPI: TheMovieDBAPI,
                            private val tmdbMovieResolver: TMDBMovieResolver
) : ExternalService {

    override fun getMovie(title: String, year: String): TMDBMovieResponse {
        val callResponse = getTMDBMovieFromService(title)
        return tmdbMovieResolver.getMovie(callResponse.body(),year)
    }

    private fun getTMDBMovieFromService(title: String): Response<String> {
        return tmdbAPI.getTerm(title).execute()
    }

}
