package ayds.dodo.movieinfo.moredetails.controller

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModel
import ayds.dodo.movieinfo.moredetails.view.HyperLinkAction
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsUiEvent
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsView
import ayds.observer.Observer
import java.awt.Desktop
import java.net.URL

interface MoreDetailsController{
    fun createMoreDetails(movie: OmdbMovie)
}

internal class MoreDetailsControllerImpl(
        private val moreDetailsView: MoreDetailsView,
        private val moreDetailsModel: MoreDetailsModel
) : MoreDetailsController{

    private val observer: Observer<MoreDetailsUiEvent> = object : Observer<MoreDetailsUiEvent> {
        override fun update(value: MoreDetailsUiEvent) {
            when (value) {
                is HyperLinkAction -> onPosterPathAction()
            }
        }
    }

    init {
        moreDetailsView.onUiEvent().subscribe(observer)
    }

    override fun createMoreDetails(movie: OmdbMovie){
        Thread {
            moreDetailsModel.searchMovie (movie.title,movie.year)
            moreDetailsModel.getLastMovie()?.let { moreDetailsView.openView(it) }
        }.start()
    }

    private fun onPosterPathAction() {
        val desktop = Desktop.getDesktop()
        try {
            val url = URL(moreDetailsModel.getLastMovie()?.posterPath)
            desktop.browse(url.toURI())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}