package ayds.dodo.movieinfo.main

import ayds.dodo.movieinfo.home.controller.HomeControllerModule
import ayds.dodo.movieinfo.home.view.HomeViewModule
import ayds.dodo.movieinfo.moredetails.controller.MoreDetailsController
import ayds.dodo.movieinfo.moredetails.controller.MoreDetailsControllerModule

fun main(args: Array<String>) {
    initGraph()
    HomeViewModule.homeView.openView()
}

private fun initGraph() {
    HomeControllerModule.init()
    MoreDetailsControllerModule.init()
}
