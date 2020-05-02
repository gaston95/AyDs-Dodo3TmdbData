package ayds.dodo.movieinfo.home.controller

import ayds.dodo.movieinfo.home.model.HomeModelModule.homeModel
import ayds.dodo.movieinfo.home.view.HomeViewModule.homeView

object HomeControllerModule {

    fun init() {
        HomeControllerImpl(homeView, homeModel)
    }
}