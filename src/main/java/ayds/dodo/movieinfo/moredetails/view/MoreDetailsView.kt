package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.home.view.UiEvent
import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.observer.Observable

interface MoreDetailsView {
    fun openView(movieData: TMDBMovie)

}