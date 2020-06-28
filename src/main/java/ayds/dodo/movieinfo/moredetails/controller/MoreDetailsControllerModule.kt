package ayds.dodo.movieinfo.moredetails.controller

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModelModule.moreDetailsModel
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsViewModule.moreDetailsView

object MoreDetailsControllerModule {
    private lateinit var moreDetailsController: MoreDetailsController

    fun init(){
        moreDetailsController = MoreDetailsControllerImpl(moreDetailsView, moreDetailsModel)
    }

    fun createMoreDetails(movie: OmdbMovie){
        moreDetailsController.createMoreDetails(movie)
    }
}