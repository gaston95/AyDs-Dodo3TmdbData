package ayds.dodo.movieinfo.home.model.repository.external

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface ExternalService {

    fun getMovie(title: String): OmdbMovie
}