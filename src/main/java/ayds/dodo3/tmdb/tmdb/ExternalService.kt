package ayds.dodo3.tmdb.tmdb



import ayds.dodo3.tmdb.tmdb.TMDBMovieResponse

interface ExternalService {
    fun getMovie(title: String , year : String): TMDBMovieResponse
}
