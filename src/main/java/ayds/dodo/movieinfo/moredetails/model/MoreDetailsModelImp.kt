package ayds.dodo.movieinfo.moredetails.model

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.TMDBRepository

interface MoreDetailsModel{

    fun searchMovie(title: String, year: String)

    fun getLastMovie(): TMDBMovie?
}

internal class MoreDetailsModelImpl(private val repository: TMDBRepository) : MoreDetailsModel {

    private var lastMovie: TMDBMovie? = null

    override fun searchMovie(title: String, year: String) {
        lastMovie = repository.getMovie(title, year)
    }

    override fun getLastMovie(): TMDBMovie? = lastMovie
}
