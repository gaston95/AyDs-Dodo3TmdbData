package ayds.dodo.movieinfo.moredetails.fulllogic

object SQLQueries {
    const val EXTRAINFO_DB_URL = "jdbc:sqlite:./extra_info.db"
    const val INFO_TABLE = "info"
    const val IMAGEURL_COLUMN = "image_url"
    const val ID_COLUMN = "id"
    const val TITLE_COLUMN = "title"
    const val PLOT_COLUMN = "plot"
    const val SOURCE_COLUMN = "source"

    const val CREATE_INFO_TABLE = "create table if not exists " + INFO_TABLE + "(" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE_COLUMN + " string, " +
            PLOT_COLUMN + " string, " +
            IMAGEURL_COLUMN + " string, " +
            SOURCE_COLUMN + " integer)"

     fun getInfoMovieByTitleQuery(title: String): String =
            ("select * from info WHERE title = '${title.replaceQuotes()}'")

    fun getInsertMovieInfoQuery(movie: TMDBMovie): String =
            "insert into info values(null," +
                    " '${movie.title.replaceQuotes()}'," +
                    " '${movie.plot.replaceQuotes()}'," +
                    " '${movie.imageUrl}'," +
                    " 1)"

    private fun String.replaceQuotes() = this.replace("'", "''")
}