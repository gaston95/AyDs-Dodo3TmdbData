package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.observer.Observable

interface MoreDetailsView {
    fun openView()
    fun onUiEvent(): Observable<MoreDetailsUiEvent>
}