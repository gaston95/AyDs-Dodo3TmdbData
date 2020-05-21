package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.EmptyMovie
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
       if (movie.isEmptyMovie()) "Movie not found" else movie.createMovieString()

    private fun OmdbMovie.isEmptyMovie() = this == EmptyMovie || this.title.isEmpty()

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
            val ratingView = RatingViewFactory.getRatingViews(it)
            allRatings.append(ratingView.getRatingTitle())
                .append(ratingSeparator)
                .append(ratingView.getRatingScore())
                .append(singleLineBreak)
        }
        return allRatings.toString()
    }
}