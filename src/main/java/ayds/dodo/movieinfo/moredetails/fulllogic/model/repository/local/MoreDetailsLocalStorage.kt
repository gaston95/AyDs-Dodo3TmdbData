package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.TMDBMovie

interface MoreDetailsLocalStorage {

    fun saveMovieInfo( movie: TMDBMovie)
    fun getMovieInfo(term: String): TMDBMovie?
}