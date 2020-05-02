package ayds.dodo.movieinfo.home.model.repository.external.omdb

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbAPI {
    @GET("?apikey=29e0bc4a")
    fun getTerm(@Query("t") title: String): Call<String>
}