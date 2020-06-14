package ayds.dodo.movieinfo.moredetails.model

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.TMDBRepository

interface MoreDetailsModel{

    fun searchMovie(title: String, year: String) : TMDBMovie

}

internal class MoreDetailsModelImpl(private val repository: TMDBRepository) : MoreDetailsModel {

    override fun searchMovie(title: String, year: String) =
         repository.getMovie(title, year)
}
