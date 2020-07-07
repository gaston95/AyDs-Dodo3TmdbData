package ayds.dodo3.tmdb



import retrofit2.Response

internal class TMDBServiceImpl (private val tmdbAPI: TheMovieDBAPI,
                                private val tmdbMovieResolver: TMDBMovieResolver
) : TMDBService {

    override fun getMovie(title: String, year: String): TMDBMovie {
        val callResponse = getTMDBMovieFromService(title)
        return tmdbMovieResolver.getMovie(callResponse.body(),year)
    }

    private fun getTMDBMovieFromService(title: String): Response<String> {
        return tmdbAPI.getTerm(title).execute()
    }

}
