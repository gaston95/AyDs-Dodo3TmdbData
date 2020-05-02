package ayds.dodo.movieinfo.home.model.repository.local

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface LocalStorage {

    fun saveMovie(term: String, movie: OmdbMovie)
    fun getMovie(term: String): OmdbMovie?
}