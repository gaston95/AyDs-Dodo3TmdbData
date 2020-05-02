package ayds.dodo.movieinfo.home.model

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.repository.OmdbRepository
import ayds.observer.Observable
import ayds.observer.Subject

interface HomeModel {

    fun searchMovie(title: String)

    fun movieObservable(): Observable<OmdbMovie>

    fun getLastMovie(): OmdbMovie?
}

internal class HomeModelImpl(private val repository: OmdbRepository) : HomeModel {

    private val movieSubject = Subject<OmdbMovie>()

    override fun searchMovie(title: String) {
        repository.getMovie(title)?.let {
            movieSubject.notify(it)
        }
    }

    override fun movieObservable(): Observable<OmdbMovie> = movieSubject

    override fun getLastMovie(): OmdbMovie? = movieSubject.lastValue()
}