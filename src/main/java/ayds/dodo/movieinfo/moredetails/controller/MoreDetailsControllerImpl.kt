package ayds.dodo.movieinfo.moredetails.controller

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModel
import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModelModule
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsView

interface MoreDetailsController{
    fun createMoreDetails(movie: OmdbMovie)
}

internal class MoreDetailsControllerImpl(
        private val moreDetailsView: MoreDetailsView,
        private val moreDetailsModel: MoreDetailsModel
) : MoreDetailsController{

    override fun createMoreDetails(movie: OmdbMovie){
        Thread {
            val movieData = moreDetailsModel.searchMovie (movie.title,movie.year)
            moreDetailsView.openView(movieData)
        }.start()
    }

}