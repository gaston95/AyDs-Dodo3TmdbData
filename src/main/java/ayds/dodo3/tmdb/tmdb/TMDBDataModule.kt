package ayds.dodo3.tmdb.tmdb

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object TMDBDataModule {

    private const val apiUrl = "https://api.themoviedb.org/3/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private fun getTMDBAPI(): TheMovieDBAPI = retrofit.create(TheMovieDBAPI::class.java)

    val tmdbService: ExternalService = TMDBService(getTMDBAPI(),
            TMDBMovieResolverImp())
}