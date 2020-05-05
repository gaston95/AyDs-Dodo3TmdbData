package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie

interface MovieDescriptionHelper {
    fun getMovieDescriptionText(movie: OmdbMovie): String
}

internal class MovieDescriptionHelperImpl : MovieDescriptionHelper {
    override fun getMovieDescriptionText(movie: OmdbMovie): String {
        return if (movie.title.isEmpty()) {
            "Movie not found"
        } else {
            val ratings = StringBuilder()
            for (rating in movie.ratings) {
                when (rating.source) {
                    "Internet Movie Database" -> {
                        val score = rating.value.split("/").toTypedArray()
                        ratings.append("IMDB").append(" ").append(score[0]).append("\n")
                    }
                    "Rotten Tomatoes" -> ratings.append(rating.source).append(" ").append(rating.value).append(
                        "\n"
                    )
                    "Metacritic" -> {
                        val score = rating.value.split("/").toTypedArray()
                        ratings.append(rating.source).append(" ").append(score[0]).append("%").append("\n")
                    }
                    else -> ratings.append(rating.source).append(" ").append(rating.value).append("\n")
                }
            }
            var title = movie.title
            if (movie.isLocallyStoraged) {
                title = "[*]" + movie.title
            }
            ("<html><body style='width: 400px'>" +
                    title + " - " + movie.year + "<br><br>"
                    + "Director: " + movie.director + "<br><br>"
                    + "Actors: " + movie.actors + "<br><br>"
                    + "Ratings: <br>" + ratings.toString() + "<br>"
                    + movie.plot)
        }
    }
}