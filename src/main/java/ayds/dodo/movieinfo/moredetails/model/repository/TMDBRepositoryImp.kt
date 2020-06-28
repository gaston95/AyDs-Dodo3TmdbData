package ayds.dodo.movieinfo.moredetails.model.repository

import ayds.dodo.movieinfo.moredetails.model.entities.DefaultMovie
import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.external.ExternalService
import ayds.dodo.movieinfo.moredetails.model.repository.local.MoreDetailsLocalStorage
import java.lang.Exception

class TMDBRepositoryImp (
        private val localStorage: MoreDetailsLocalStorage,
        private val externalService: ExternalService
) : TMDBRepository {

    override fun getMovie(title: String, year: String): TMDBMovie? {
        var movie = localStorage.getMovieInfo(title)

        when {
            movie != null -> markMovieAsLocal(movie)
            else -> {
                try {
                    movie = externalService.getMovie(title, year)
                    localStorage.saveMovieInfo(movie)
                }
                catch (e:Exception){
                    movie = DefaultMovie
                }
            }
        }
        return movie
    }

    private fun markMovieAsLocal(movie: TMDBMovie) {
        movie.isLocallyStoraged = true
    }
}