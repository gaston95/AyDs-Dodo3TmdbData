package ayds.dodo.movieinfo.home.model

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.repository.OmdbRepository
import ayds.dodo.movieinfo.home.view.UiEvent
import ayds.observer.Observable
import ayds.observer.Subject

interface HomeModel {

    fun searchMovie(title: String)

    fun movieObservable(): Observable<OmdbMovie>

    fun connectionObservable() : Observable<UiEvent>

    fun getLastMovie(): OmdbMovie?
}

internal class HomeModelImpl(private val repository: OmdbRepository) : HomeModel {

    private val movieSubject = Subject<OmdbMovie>()
    private val connectionSubject = Subject<UiEvent>()

    override fun searchMovie(title: String) {
        try {
            repository.getMovie(title)?.let {
                movieSubject.notify(it)
            }
        }
        catch (e: Exception){
            connectionSubject.notify(UiEvent.NO_INTERNET)

        }
    }

    override fun movieObservable(): Observable<OmdbMovie> = movieSubject

    override fun connectionObservable(): Observable<UiEvent> = connectionSubject

    override fun getLastMovie(): OmdbMovie? = movieSubject.lastValue()
}