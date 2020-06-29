package ayds.dodo3.tmdb.external.tmdb

open class TMDBMovieResponse {
    open var title = ""
    open var plot = ""
    open var imageUrl = ""
    open var posterPath: String? = null
    open var isLocallyStoraged = false
}

object DefaultMovie: TMDBMovieResponse() {
    override var title = "No results"
    override var plot = "No results"
    override var imageUrl = "https://www.themoviedb.org/assets/2/v4/logos/" +
    "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
}