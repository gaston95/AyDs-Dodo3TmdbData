package ayds.dodo3.tmdb.external



import ayds.dodo3.tmdb.external.tmdb.TMDBMovieResponse

interface ExternalService {
    fun getMovie(title: String , year : String): TMDBMovieResponse
}
