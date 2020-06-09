package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository

import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.external.ExternalService
import ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.MoreDetailsLocalStorage

class TMDBRepositoryImp (
        private val localStorage: MoreDetailsLocalStorage,
        private val externalService: ExternalService
) : TMDBRepository{

    private val localMovie = "[*]"

    override fun getMovie(title: String, year: String): TMDBMovie {
        val movieData = localStorage.getMovieInfo(title)
        return movieData?.let { getMovieMarkedAsLocallyStored(it) } ?: externalService.getMovie(title,year)
    }

    private fun getMovieMarkedAsLocallyStored(movie: TMDBMovie): TMDBMovie {
        movie.plot = localMovie + movie.plot
        return movie
    }



}