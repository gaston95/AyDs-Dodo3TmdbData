package ayds.dodo.movieinfo.moredetails.model.repository.external


import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

interface ExternalService {
    fun getMovie(title: String , year : String): TMDBMovie
}
