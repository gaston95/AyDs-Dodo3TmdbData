package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.repository.local.sqldb.SqlQueries
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
            connection = DriverManager.getConnection(SqlQueries.EXTRAINFO_DB_URL)
        } catch (e: SQLException) {
            println("Could not create connection $SqlQueries.EXTRAINFO_DB_URL $e")
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
            statement?.executeUpdate(SqlQueries.CREATE_INFO_TABLE)
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    private fun createInfoTableIfNeeded(){
        if(!isInfoTableCreated())
            createInfoTable()
    }

    @JvmStatic
    fun createNewDatabase() {
        openConnectionToExtraInfo()
        createInfoTableIfNeeded()
        closeConnectionToExtraInfo()
    }
    
    private fun String.replaceQuotes() = this.replace("'", "''")

    private fun getInsertMovieInfoQuery(title:String, plot: String, imageUrl: String): String =
            "insert into info values(null," +
                    " '${title.replaceQuotes()}'," +
                    " '${plot.replaceQuotes()}'," +
                    " '$imageUrl'," +
                    " 1)"

    private fun insertMovieInfo(title:String, plot: String, imageUrl: String){
        try {
            statement?.executeUpdate(getInsertMovieInfoQuery(title, plot,imageUrl))
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    @JvmStatic
    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        openConnectionToExtraInfo()
        insertMovieInfo(title,plot,imageUrl)
        closeConnectionToExtraInfo()
    }

    private fun getInfoMovieByTitleQuery(title: String): String =
            ("select * from info WHERE title = '${title.replaceQuotes()}'")

    private fun getMovieColumn(title: String, column: String): String? {
        var plot: String? = null
        try {
            val movieInfoResultSet = statement?.executeQuery(getInfoMovieByTitleQuery(title))
            movieInfoResultSet?.let {
                if(it.next())
                    plot = it.getString(column)
            }
        } catch (e: SQLException) {
            System.err.println("Get movie plot error " + e.message)
        }
        return plot
    }

    @JvmStatic
    fun getOverview(title: String): String? {
        openConnectionToExtraInfo()
        val overview = getMovieColumn(title, SqlQueries.PLOT_COLUMN)
        closeConnectionToExtraInfo()
        return overview
    }

    @JvmStatic
    fun getImageUrl(title: String): String? {
        openConnectionToExtraInfo()
        val imageUrl = getMovieColumn(title, SqlQueries.IMAGEURL_COLUMN)
        closeConnectionToExtraInfo()
        return imageUrl
    }
}
