package ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.model.repository.TMDBRepositoryImp
import ayds.dodo.movieinfo.moredetails.model.repository.local.sqldb.SQLQueriesImp
import ayds.dodo.movieinfo.moredetails.model.repository.local.sqldb.SqlDBImp
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class TMDBMovieResolver(val movie: OmdbMovie) {

    private val apiUrl = "https://api.themoviedb.org/3/"
    private val tmdbAPI = createAPI()
    private val querys = SQLQueriesImp()
    private val localStorage = SqlDBImp(querys)
    private val exteralService = TMDBService(tmdbAPI, this)

    private fun createAPI(): TheMovieDBAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        return retrofit.create(TheMovieDBAPI::class.java)
    }

    fun getMovie() = TMDBRepositoryImp(localStorage, exteralService).getMovie(movie.title,movie.year)

}