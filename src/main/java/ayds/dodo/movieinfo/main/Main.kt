package ayds.dodo.movieinfo.main

import ayds.dodo.movieinfo.home.controller.HomeControllerModule
import ayds.dodo.movieinfo.home.view.HomeViewModule

fun main(args: Array<String>) {
    initGraph()
    HomeViewModule.homeView.openView()
}

private fun initGraph() {
    HomeControllerModule.init()
}
