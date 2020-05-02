package ayds.dodo.movieinfo.moredetails.fulllogic

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMovieDBAPI {
    @GET("search/movie?api_key=d18da1b5da16397619c688b0263cd281&include_adult=false")
    fun getTerm(@Query("query") term: String?): Call<String?>?
}