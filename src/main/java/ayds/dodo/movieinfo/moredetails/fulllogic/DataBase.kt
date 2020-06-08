package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import java.sql.*

object DataBase {
    private lateinit var connection: Connection
    private val statement: Statement?
        get() {
            var statement: Statement? = null
            try {
                statement = connection.createStatement()
                statement.queryTimeout = 30
            } catch (e: SQLException) {
                println("Create statement error " + e.message)
            }
            return statement
        }

    private fun openConnectionToExtraInfo() {
        try {
            connection = DriverManager.getConnection(SQLQueries.EXTRAINFO_DB_URL)
        } catch (e: SQLException) {
            println("Could not create connection ${SQLQueries.EXTRAINFO_DB_URL} $e")
        }
    }

    private fun closeConnectionToExtraInfo() {
        try {
            connection.close()
        } catch (e: SQLException) {
            System.err.println(e)
        }
    }

    private fun isInfoTableCreated(): Boolean {
        try {
            val tables = connection.metaData?.getTables(null, null, "info", null)
            return tables?.next() ?: false
        } catch (e: SQLException) {
        }
        return false
    }

    private fun createInfoTable(){
        try {
            statement?.executeUpdate(SQLQueries.CREATE_INFO_TABLE)
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    private fun createInfoTableIfNeeded(){
        if(!isInfoTableCreated())
            createInfoTable()
    }

    fun createNewDatabase() {
        openConnectionToExtraInfo()
        createInfoTableIfNeeded()
        closeConnectionToExtraInfo()
    }
    

    private fun insertMovieInfo(movie: TMDBMovie){
        try {
            statement?.executeUpdate(SQLQueries.getInsertMovieInfoQuery(movie))
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    fun saveMovieInfo(movie: TMDBMovie) {
        openConnectionToExtraInfo()
        insertMovieInfo(movie)
        closeConnectionToExtraInfo()
    }

    fun getMovieInfo(title: String): TMDBMovie? {
        openConnectionToExtraInfo()
        val movie = selectMovie(title)
        closeConnectionToExtraInfo()
        return movie
    }

    private fun selectMovie(title: String): TMDBMovie? {
        var movie: TMDBMovie? = null
        try {
            val titleSql = title.replace("'","''")
            val moviesResultSet = statement?.executeQuery(SQLQueries.getInfoMovieByTitleQuery(titleSql))
            movie = moviesResultSet?.let { SQLQueries.resultSetToMovieMapper(it) }
        } catch (e: SQLException) {
            System.err.println("Get movie by title error " + e.message)
        }
        return movie
    }
}