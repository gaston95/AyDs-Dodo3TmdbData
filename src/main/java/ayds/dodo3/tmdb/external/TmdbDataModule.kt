package ayds.dodo3.tmdb.external

import ayds.dodo3.tmdb.external.tmdb.TMDBMovieResolverImp
import ayds.dodo3.tmdb.external.tmdb.TMDBService
import ayds.dodo3.tmdb.external.tmdb.TheMovieDBAPI
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object TmdbDataModule {

    private const val apiUrl = "https://api.themoviedb.org/3/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private fun getTMDBAPI(): TheMovieDBAPI = retrofit.create(TheMovieDBAPI::class.java)

    val tmdbService:ExternalService= TMDBService(getTMDBAPI(),
        TMDBMovieResolverImp())
}