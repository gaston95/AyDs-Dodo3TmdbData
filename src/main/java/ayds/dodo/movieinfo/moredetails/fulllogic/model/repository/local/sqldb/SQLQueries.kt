package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.sqldb

import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import java.sql.ResultSet


internal interface SQLQueries {

    fun getInfoMovieByTitleQuery(title: String): String
    fun getInsertMovieInfoQuery(movie: TMDBMovie): String
    fun resultSetToMovieMapper(resultSet: ResultSet): TMDBMovie?

    companion object {
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

    }
}