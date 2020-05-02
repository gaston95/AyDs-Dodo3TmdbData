package ayds.dodo.movieinfo.home.model.repository.local.sqldb

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.entities.Rating
import java.sql.ResultSet

internal interface SqlQueries {

    fun getInsertMovieQuery(term: String, movie: OmdbMovie): String
    fun getInsertRatingQuery(movie: OmdbMovie, rating: Rating): String
    fun getSelectMoviesByTermQuery(term: String): String
    fun getSelectRatingsByMovieQuery(movie: OmdbMovie): String
    fun resultSetToMovieMapper(resultSet: ResultSet): OmdbMovie?
    fun resultSetToRatingsMapper(resultSet: ResultSet): List<Rating>

    companion object {
        const val DB_URL = "jdbc:sqlite:./movies.db"
        const val MOVIES_TABLE = "movies"
        const val RATINGS_TABLE = "ratings"
        const val ID_COLUMN = "id"
        const val TERM_COLUMN = "term"
        const val TITLE_COLUMN = "title"
        const val YEAR_COLUMN = "year"
        const val PLOT_COLUMN = "plot"
        const val DIRECTOR_COLUMN = "director"
        const val ACTORS_COLUMN = "actors"
        const val POSTER_URL_COLUMN = "posterUrl"
        const val SOURCE_COLUMN = "source"
        const val VALUE_COLUMN = "value"
        const val CREATE_MOVIES_TABLE = "create table " + MOVIES_TABLE + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TERM_COLUMN + " string, " +
                TITLE_COLUMN + " string, " +
                YEAR_COLUMN + " integer," +
                PLOT_COLUMN + " string, " +
                DIRECTOR_COLUMN + " string, " +
                ACTORS_COLUMN + " string, " +
                POSTER_URL_COLUMN + " string" +
                ")"
        const val CREATE_RATINGS_TABLE = "create table " + RATINGS_TABLE + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_COLUMN + " string, " +
                SOURCE_COLUMN + " string, " +
                VALUE_COLUMN + " string" +
                ")"
    }
}