package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository

import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie


interface TMDBRepository {
    fun getMovie(title: String): TMDBMovie?
}
