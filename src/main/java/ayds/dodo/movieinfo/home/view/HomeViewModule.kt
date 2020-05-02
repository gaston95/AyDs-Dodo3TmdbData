package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.HomeModelModule

object HomeViewModule {
    val homeView = MainWindow(HomeModelModule.homeModel, MovieDescriptionHelperImpl())
}