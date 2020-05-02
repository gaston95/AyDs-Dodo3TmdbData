package ayds.dodo.movieinfo.home.model.repository.local.sqldb

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.entities.Rating
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

internal class SqlQueriesImpl : SqlQueries {

    override fun getInsertMovieQuery(term: String, movie: OmdbMovie): String =
        "insert into ${SqlQueries.MOVIES_TABLE} values(null, " +
                "'$term', " +
                "'${getEscapedString(movie.title)}', " +
                "'${movie.year}'," +
                " '${getEscapedString(movie.plot)}', " +
                "'${getEscapedString(movie.director)}'," +
                " '${getEscapedString(movie.actors)}', " +
                "'${movie.posterUrl}')"

    override fun getInsertRatingQuery(movie: OmdbMovie, rating: Rating) =
        "insert into ${SqlQueries.RATINGS_TABLE} values(null, '${movie.title}', '${rating.source}', '${rating.value}')"

    override fun getSelectMoviesByTermQuery(term: String) =
        "select * from ${SqlQueries.MOVIES_TABLE} WHERE ${SqlQueries.TERM_COLUMN} = '$term'"

    override fun getSelectRatingsByMovieQuery(movie: OmdbMovie) =
        "select * from ${SqlQueries.RATINGS_TABLE} WHERE ${SqlQueries.TITLE_COLUMN} = '${movie.title}'"

    override fun resultSetToMovieMapper(resultSet: ResultSet): OmdbMovie? =
        try {
            if (resultSet.next()) {
                OmdbMovie().apply {
                    title = resultSet.getString(SqlQueries.TITLE_COLUMN)
                    year = resultSet.getString(SqlQueries.YEAR_COLUMN)
                    plot = resultSet.getString(SqlQueries.PLOT_COLUMN)
                    director = resultSet.getString(SqlQueries.DIRECTOR_COLUMN)
                    actors = resultSet.getString(SqlQueries.ACTORS_COLUMN)
                    posterUrl = resultSet.getString(SqlQueries.POSTER_URL_COLUMN)
                }
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }

    override fun resultSetToRatingsMapper(resultSet: ResultSet): List<Rating> {
        val ratings = ArrayList<Rating>()
        try {
            while (resultSet.next()) {
                ratings.add(
                    Rating().apply {
                        source = resultSet.getString(SqlQueries.SOURCE_COLUMN)
                        value = resultSet.getString(SqlQueries.VALUE_COLUMN)
                    })
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return ratings
    }

    private fun getEscapedString(string: String): String =
        string.replace("'".toRegex(), "''")
}