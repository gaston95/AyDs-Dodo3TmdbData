package ayds.dodo.movieinfo.moredetails.controller

import ayds.dodo.movieinfo.moredetails.model.MoreDetailsModelModule.MoreDetailsModel
import ayds.dodo.movieinfo.moredetails.view.MoreDetailsModule.moreDetailsView

object MoreDetailsControllerModule {

    fun init(){
        MoreDetailsControllerImpl( moreDetailsView, MoreDetailsModel)
    }
}