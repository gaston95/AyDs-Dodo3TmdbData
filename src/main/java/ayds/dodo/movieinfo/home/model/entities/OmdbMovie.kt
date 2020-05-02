package ayds.dodo.movieinfo.home.model.entities

import java.util.*

class OmdbMovie {
    var title = ""
    var year = ""
    var plot = ""
    var director = ""
    var actors = ""
    var posterUrl = "https://i.picsum.photos/id/355/267/179.jpg"
    var isLocallyStoraged = false
    var ratings: List<Rating> = ArrayList()
}