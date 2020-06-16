package ayds.dodo.movieinfo.moredetails.model.repository.local.sqldb

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.model.repository.local.MoreDetailsLocalStorage
import ayds.dodo.movieinfo.utils.sql.SqlDB
import java.sql.SQLException

internal class SqlDBImp (private val SqlQueries: SQLQueries) : SqlDB(), MoreDetailsLocalStorage {
        override val dbUrl = SQLQueries.EXTRAINFO_DB_URL

    init {
        initDatabase()
    }

    private fun initDatabase() {
        openConnection()
        createInfoTableIfNeeded()
        closeConnection()
    }
    private fun createInfoTable(){
        try {
            statement?.executeUpdate(SQLQueries.CREATE_INFO_TABLE)
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    private fun createInfoTableIfNeeded(){
        if(!tableCreated(SQLQueries.INFO_TABLE))
            createInfoTable()
    }


    private fun insertMovieInfo(movie: TMDBMovie){
        try {
            statement?.executeUpdate(SqlQueries.getInsertMovieInfoQuery(movie))
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    override fun saveMovieInfo(movie: TMDBMovie) {
        openConnection()
        insertMovieInfo(movie)
        closeConnection()
    }

    override fun getMovieInfo(term: String): TMDBMovie? {
        openConnection()
        val movie = selectMovie(term)
        closeConnection()
        return movie
    }

    private fun selectMovie(title: String): TMDBMovie? {
        var movie: TMDBMovie? = null
        try {
            val titleSql = title.replace("'","''")
            val moviesResultSet = statement?.executeQuery(SqlQueries.getInfoMovieByTitleQuery(titleSql))
            movie = moviesResultSet?.let { SqlQueries.resultSetToMovieMapper(it) }
        } catch (e: SQLException) {
            System.err.println("Get movie by title error " + e.message)
        }
        return movie
    }
}