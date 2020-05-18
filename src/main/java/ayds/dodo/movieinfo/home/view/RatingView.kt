package ayds.dodo.movieinfo.home.view

import ayds.dodo.movieinfo.home.model.entities.Rating

interface RatingView {

    fun getRatingTitle(): String
    fun getRatingScore(): String
}

object RatingViewFactory {

    fun get(rating: Rating): RatingView =
            when(rating.source) {
                "Internet Movie Database" -> IMDBRatingView(rating)
                "Metacritic" -> MetacriticView(rating)
                else -> DefaultRatingview(rating)
            }
}

class IMDBRatingView(private val rating: Rating): RatingView {

    override fun getRatingTitle(): String = "IMDB"

    override fun getRatingScore(): String {
        val score = rating.value.split("/").toTypedArray()
        return score[0]
    }
}

class MetacriticView(private val rating: Rating): RatingView {

    override fun getRatingTitle(): String = "Metacritic"

    override fun getRatingScore(): String {
        val score = rating.value.split("/").toTypedArray()
        return score[0] + "%"
    }
}

class DefaultRatingview(private val rating: Rating): RatingView {

    override fun getRatingTitle(): String = rating.source

    override fun getRatingScore(): String = rating.value
}