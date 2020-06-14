package ayds.dodo.movieinfo.moredetails.model.repository

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie


interface TMDBRepository {
    fun getMovie(title: String, year: String): TMDBMovie
}
