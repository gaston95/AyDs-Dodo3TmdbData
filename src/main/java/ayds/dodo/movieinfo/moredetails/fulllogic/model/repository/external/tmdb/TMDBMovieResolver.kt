package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.external.tmdb

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase
import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.DefaultMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.HTMLFormatter
import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class TMDBMovieResolver(val movie: OmdbMovie) {

    private val apiUrl = "https://api.themoviedb.org/3/"
    private val tmdbAPI : TheMovieDBAPI

    init {
        tmdbAPI = createAPI()
    }

    private fun createAPI(): TheMovieDBAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        return retrofit.create(TheMovieDBAPI::class.java)
    }


}