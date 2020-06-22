package ayds.dodo.movieinfo.moredetails.model

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.TMDBRepository
import ayds.observer.Observable
import ayds.observer.Subject

interface MoreDetailsModel{

    fun searchMovie(title: String, year: String)

    fun movieObservable() : Observable<TMDBMovie>
}

internal class MoreDetailsModelImpl(private val repository: TMDBRepository) : MoreDetailsModel {

    private var movieSubject = Subject<TMDBMovie>()

    override fun searchMovie(title: String, year: String) {
        repository.getMovie(title, year).let {
            movieSubject.notify(it)
        }
    }

    override fun movieObservable(): Observable<TMDBMovie> = movieSubject



}
