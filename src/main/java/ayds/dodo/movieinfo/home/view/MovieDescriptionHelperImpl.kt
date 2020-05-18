package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import javax.management.openmbean.OpenMBeanAttributeInfo

interface MovieDescriptionHelper {
    fun getMovieDescriptionText(movie: OmdbMovie): String
}

internal class MovieDescriptionHelperImpl : MovieDescriptionHelper {

    private val htmlHeading = "<html><body style=\"width: 400px\">"
    private val singleLineBreak = "<br>"
    private val doublieLineBreak = "<br><br>"
    private val localMovie = "[*]"
    private val emptyString = ""
    private val ratingSeparator = ": "

    override fun getMovieDescriptionText(movie: OmdbMovie): String =
       if(movie.isEmptyMovie())
           "Movie not Found"
       else
           movie.createMovieString()

    private fun OmdbMovie.isEmptyMovie() = this.title.isEmpty()

    private fun OmdbMovie.createMovieString() =
        (htmlHeading
                + getTitle() + " - " + year + doublieLineBreak
                + "Runtime: " + runtime + doublieLineBreak
                + "Director: " + director + doublieLineBreak
                + "Actors: " + actors + doublieLineBreak
                + "Ratings: "+ singleLineBreak + getRatings(this) + doublieLineBreak
                + plot)

    private fun OmdbMovie.getTitle() =
            when {
                isLocallyStoraged -> localMovie + title
                else -> title
            }

    private fun getRatings(movie: OmdbMovie): String {
        val allRatings = StringBuilder()

        movie.ratings.forEach{
            val ratingView = RatingViewFactory.get(it)
            allRatings.append(ratingView.getRatingTitle())
                .append(ratingSeparator)
                .append(ratingView.getRatingScore())
                .append(singleLineBreak)
        }

        return allRatings.toString()
    }


}