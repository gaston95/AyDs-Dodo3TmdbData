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
                + "Ratings: <br>" + getRatingBuilder(movie).toString() + "<br>"
                + movie.plot)

    private fun getTitle(movie: OmdbMovie): String {
        var title = movie.title
        if (movie.isLocallyStoraged)
            title = "[*]" + movie.title
        return title
    }

    private fun getRatingBuilder(movie: OmdbMovie): StringBuilder {
        val allRatings = StringBuilder()
        for (rating in movie.ratings) {
            allRatings.append(getRating(rating))
        }
        return allRatings
    }

    private fun getRating(rating: Rating): String {
        var stringRating = ""
        when (rating.source) {
            "Internet Movie Database" -> return setIMDBRating(rating)

            "Metacritic" -> return setMetacriticRating(rating)

            else -> return setOtherRating(rating)
        }
    }

    private fun setIMDBRating(rating: Rating): String {
        return "IMDB ${(rating.value.split("/").toTypedArray())[0]} \n"
    }

    private fun setMetacriticRating(rating: Rating): String {
        return "${rating.source} ${(rating.value.split("/").toTypedArray())[0]}% \n"
    }

    private fun setOtherRating(rating: Rating): String {
        return "${rating.source} ${rating.value} \n"
    }
}