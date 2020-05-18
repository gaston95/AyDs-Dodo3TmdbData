package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.NullMovie
import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface MovieDescriptionHelper {
    fun getMovieDescriptionText(movie: OmdbMovie): String
}

internal class MovieDescriptionHelperImpl : MovieDescriptionHelper {

    private val htmlHeading = "<html><body style=\"width: 400px\">"
    private val singleLineBreak = "<br>"
    private val doubleLineBreak = "<br><br>"
    private val localMovie = "[*]"
    private val ratingSeparator = ": "

    override fun getMovieDescriptionText(movie: OmdbMovie): String =
       when {
           movie.isNullMovie() -> "Connection error"
           movie.isEmptyMovie() -> "Movie not found"
           else -> movie.createMovieString()
       }


    private fun OmdbMovie.isNullMovie() = this == NullMovie

    private fun OmdbMovie.isEmptyMovie() = this.title.isEmpty()

    private fun OmdbMovie.createMovieString() =
        (htmlHeading
                + getTitle() + " - " + year + doubleLineBreak
                + "Runtime: " + runtime + doubleLineBreak
                + "Director: " + director + doubleLineBreak
                + "Actors: " + actors + doubleLineBreak
                + "Ratings: "+ singleLineBreak + getRatings(this) + doubleLineBreak
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