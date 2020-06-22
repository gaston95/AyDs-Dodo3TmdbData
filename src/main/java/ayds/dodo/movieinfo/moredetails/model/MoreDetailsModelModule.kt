package ayds.dodo.movieinfo.moredetails.model

import ayds.dodo.movieinfo.moredetails.model.repository.TMDBRepositoryImp
import ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb.TMDBMovieResolverImp
import ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb.TMDBService
import ayds.dodo.movieinfo.moredetails.model.repository.external.tmdb.TheMovieDBAPI
import ayds.dodo.movieinfo.moredetails.model.repository.local.sqldb.SQLQueriesImp
import ayds.dodo.movieinfo.moredetails.model.repository.local.sqldb.SqlDBImp
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object MoreDetailsModelModule {

    private const val apiUrl = "https://api.themoviedb.org/3/"

    private val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

    private fun getTMDBAPI(): TheMovieDBAPI = retrofit.create(TheMovieDBAPI::class.java)

    private val repository = TMDBRepositoryImp(
            SqlDBImp(SQLQueriesImp()), TMDBService(getTMDBAPI(),
            TMDBMovieResolverImp()
    ))

    val MoreDetailsModel: MoreDetailsModel = MoreDetailsModelImpl(repository)
}