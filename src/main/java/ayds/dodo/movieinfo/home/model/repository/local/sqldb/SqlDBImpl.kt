package ayds.dodo.movieinfo.home.model.repository.local.sqldb

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.home.model.repository.local.LocalStorage
import java.sql.SQLException

internal class SqlDBImpl(private val sqlQueries: SqlQueries) : SqlDB(), LocalStorage {

    override val dbUrl = SqlQueries.DB_URL

    init {
        initDatabase()
    }

    private fun initDatabase() {
        openConnection()
        createTablesIfNeeded()
        closeConnection()
    }

    private fun createTablesIfNeeded() {
        if (!tableCreated(SqlQueries.MOVIES_TABLE)) createTables()
    }

    private fun createTables() {
        try {
            statement?.executeUpdate(SqlQueries.CREATE_MOVIES_TABLE)
            statement?.executeUpdate(SqlQueries.CREATE_RATINGS_TABLE)
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override fun saveMovie(term: String, movie: OmdbMovie) {
        openConnection()
        insertMovie(term, movie)
        closeConnection()
    }

    private fun insertMovie(term: String, movie: OmdbMovie) {
        try {
            statement?.executeUpdate(sqlQueries.getInsertMovieQuery(term, movie))
            movie.ratings.forEach {
                statement?.executeUpdate(sqlQueries.getInsertRatingQuery(movie, it))
            }
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    override fun getMovie(term: String): OmdbMovie? {
        openConnection()
        val movie = selectMovieByTerm(term)
        closeConnection()
        return movie
    }

    private fun selectMovieByTerm(term: String): OmdbMovie? {
        var movie: OmdbMovie? = null
        try {
            val moviesResultSet = statement?.executeQuery(sqlQueries.getSelectMoviesByTermQuery(term))
            movie = moviesResultSet?.let { sqlQueries.resultSetToMovieMapper(it) }
            movie?.apply {
                val ratingsResultSet = statement?.executeQuery(sqlQueries.getSelectRatingsByMovieQuery(movie))
                ratingsResultSet?.let {  ratings = sqlQueries.resultSetToRatingsMapper(it) }
            }
        } catch (e: SQLException) {
            System.err.println("Get movie by term error " + e.message)
        }
        return movie
    }
}