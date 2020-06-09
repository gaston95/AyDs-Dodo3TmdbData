package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.sqldb

import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import java.sql.ResultSet
import java.sql.SQLException

class SQLQueriesImp : SQLQueries {

    override fun getInfoMovieByTitleQuery(title: String): String =
        ("select * from info WHERE title = '${title.replaceQuotes()}'")

    override  fun getInsertMovieInfoQuery(movie: TMDBMovie): String =
        "insert into info values(null," +
                " '${movie.title.replaceQuotes()}'," +
                " '${movie.plot.replaceQuotes()}'," +
                " '${movie.imageUrl}'," +
                " 1)"

    override fun resultSetToMovieMapper(resultSet: ResultSet): TMDBMovie? =
        try {
            if (resultSet.next()) {
                TMDBMovie().apply {
                    title = resultSet.getString(SQLQueries.TITLE_COLUMN)
                    plot = resultSet.getString(SQLQueries.PLOT_COLUMN)
                    imageUrl = resultSet.getString(SQLQueries.IMAGEURL_COLUMN)
                }
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }

     private fun String.replaceQuotes() = this.replace("'", "''")
}