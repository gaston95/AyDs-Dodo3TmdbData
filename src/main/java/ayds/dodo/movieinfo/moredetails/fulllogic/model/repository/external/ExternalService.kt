package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.external


import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie

interface ExternalService {
    fun getMovie(title: String , year : String): TMDBMovie
}
