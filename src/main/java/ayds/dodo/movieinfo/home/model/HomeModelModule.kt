package ayds.dodo.movieinfo.home.model

import ayds.dodo.movieinfo.home.model.repository.OmdbRepositoryImpl
import ayds.dodo.movieinfo.home.model.repository.external.omdb.OmdbAPI
import ayds.dodo.movieinfo.home.model.repository.external.omdb.OmdbResponseToOmdbMovieResolverImpl
import ayds.dodo.movieinfo.home.model.repository.external.omdb.OmdbService
import ayds.dodo.movieinfo.home.model.repository.local.sqldb.SqlDBImpl
import ayds.dodo.movieinfo.home.model.repository.local.sqldb.SqlQueriesImpl
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object HomeModelModule {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://www.omdbapi.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private fun getOmdbAPI(): OmdbAPI = retrofit.create(OmdbAPI::class.java)


    private val repository = OmdbRepositoryImpl(
        SqlDBImpl(SqlQueriesImpl()), OmdbService(getOmdbAPI(),
        OmdbResponseToOmdbMovieResolverImpl()
    ))

    val homeModel: HomeModel = HomeModelImpl(repository)
}