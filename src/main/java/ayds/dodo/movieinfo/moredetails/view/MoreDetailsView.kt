package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.observer.Observable

interface MoreDetailsView {
<<<<<<< HEAD
    fun openView()
    fun onUiEvent(): Observable<UiEvent>
=======
    fun openView(movieData: TMDBMovie)
    fun onUiEvent(): Observable<MoreDetailsUiEvent>
>>>>>>> c382e78cc1f4ba09f34dcf57add5f44995885a72
}