package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

interface MoreDetailsView {
    fun openView(movieData: TMDBMovie)
}