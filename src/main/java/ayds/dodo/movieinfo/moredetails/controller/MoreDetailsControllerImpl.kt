package ayds.dodo.movieinfo.moredetails.controller

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModel
import ayds.dodo.movieinfo.moredetails.view.HyperLink
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsUiEvent
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsView
import ayds.observer.Observer
import java.awt.Desktop
import javax.swing.event.HyperlinkEvent

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
                is HyperLink -> openHyperLink(value)
            }
        }
    }

    init {
        moreDetailsView.onUiEvent().subscribe(observer)
    }

    override fun createMoreDetails(movie: OmdbMovie){
        moreDetailsView.openView()
        Thread {
            moreDetailsModel.searchMovie(movie.title,movie.year)
        }.start()
    }

    private fun openHyperLink(hyperLink: HyperLink) {
        if (HyperlinkEvent.EventType.ACTIVATED == hyperLink.event.eventType) {
            val desktop = Desktop.getDesktop()
            try {
                val url = hyperLink.event.url
                desktop.browse(url.toURI())
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

}