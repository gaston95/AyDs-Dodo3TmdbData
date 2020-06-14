package ayds.dodo.movieinfo.moredetails.model.repository.local

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

interface MoreDetailsLocalStorage {

    fun saveMovieInfo( movie: TMDBMovie)
    fun getMovieInfo(term: String): TMDBMovie?
}