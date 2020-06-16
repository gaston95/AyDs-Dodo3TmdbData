package ayds.dodo.movieinfo.moredetails.model.repository

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.external.ExternalService
import ayds.dodo.movieinfo.moredetails.model.repository.local.MoreDetailsLocalStorage

class TMDBRepositoryImp (
        private val localStorage: MoreDetailsLocalStorage,
        private val externalService: ExternalService
) : TMDBRepository {

    private val localMovie = "[*]"

    override fun getMovie(title: String, year: String): TMDBMovie {
        var movie = localStorage.getMovieInfo(title)

        when {
            movie != null -> markMovieAsLocal(movie)
            else -> {
                movie = externalService.getMovie(title, year)
                localStorage.saveMovieInfo(movie)

            }
        }
        return movie
    }

    private fun markMovieAsLocal(movie: TMDBMovie):TMDBMovie {
        movie.plot = localMovie + movie.plot
        return movie
    }
}