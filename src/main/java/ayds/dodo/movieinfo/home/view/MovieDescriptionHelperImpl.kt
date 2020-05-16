package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.entities.Rating

interface MovieDescriptionHelper {
    fun getMovieDescriptionText(movie: OmdbMovie): String
}

internal class MovieDescriptionHelperImpl : MovieDescriptionHelper {
    override fun getMovieDescriptionText(movie: OmdbMovie): String {
        return if (movie.title.isEmpty()) {
            "Movie not found"
        } else {
            createMovieString(movie)
        }
    }

    private fun createMovieString(movie: OmdbMovie) =
        ("<html><body style=\"width: 400px\">"
                + getTitle(movie) + " - " + movie.year + "<br><br>"
                + "Runtime: " + movie.runtime + "<br><br>"
                + "Director: " + movie.director + "<br><br>"
                + "Actors: " + movie.actors + "<br><br>"
                + "Ratings: <br>" + getRatings(movie) + "<br>"
                + movie.plot)

    private fun getTitle(movie: OmdbMovie): String {
        var title = if (movie.isLocallyStoraged) "[*]" else ""
        title += movie.title
        return title
    }

    private fun getRatings(movie: OmdbMovie): String {
        var allRatings = ""
        for (rating in movie.ratings) {
            allRatings += getRating(rating)
        }
        return allRatings
    }

    private fun getRating(rating: Rating): String {
        return when (rating.source) {
            "Internet Movie Database" -> getIMDBRating(rating)

            "Metacritic" -> getMetacriticRating(rating)

            else -> getOtherRating(rating)
        }
    }

    private fun getIMDBRating(rating: Rating): String {
        return "IMDB ${(rating.value.split("/").toTypedArray())[0]} \n"
    }

    private fun getMetacriticRating(rating: Rating): String {
        return "${rating.source} ${(rating.value.split("/").toTypedArray())[0]}% \n"
    }

    private fun getOtherRating(rating: Rating): String {
        return "${rating.source} ${rating.value} \n"
    }
}