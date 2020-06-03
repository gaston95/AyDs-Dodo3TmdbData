package ayds.dodo.movieinfo.moredetails.fulllogic

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
    

    private fun insertMovieInfo(title:String, plot: String, imageUrl: String){
        try {
            statement?.executeUpdate(SQLQueries.getInsertMovieInfoQuery(title, plot,imageUrl))
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        openConnectionToExtraInfo()
        insertMovieInfo(title,plot,imageUrl)
        closeConnectionToExtraInfo()
    }

    private fun getMovieColumn(title: String, column: String): String? {
        var plot: String? = null
        try {
            val movieInfoResultSet = statement?.executeQuery(SQLQueries.getInfoMovieByTitleQuery(title))
            movieInfoResultSet?.let {
                if(it.next())
                    plot = it.getString(column)
            }
        } catch (e: SQLException) {
            System.err.println("Get movie plot error " + e.message)
        }
        return plot
    }

    fun getOverview(title: String): String? {
        openConnectionToExtraInfo()
        val overview = getMovieColumn(title, SQLQueries.PLOT_COLUMN)
        closeConnectionToExtraInfo()
        return overview
    }

    fun getImageUrl(title: String): String? {
        openConnectionToExtraInfo()
        val imageUrl = getMovieColumn(title, SQLQueries.IMAGEURL_COLUMN)
        closeConnectionToExtraInfo()
        return imageUrl
    }
}