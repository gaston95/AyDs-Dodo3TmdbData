package ayds.dodo3.tmdb

interface TMDBService {
    fun getMovie(title: String , year : String): TMDBMovieResponse
}
