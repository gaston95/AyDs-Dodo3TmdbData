package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface MoreDetailsView {
    fun openView(movie: OmdbMovie)
}