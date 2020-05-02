package ayds.dodo.movieinfo.home.model.repository

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface OmdbRepository {
    fun getMovie(title: String): OmdbMovie?
}