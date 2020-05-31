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

    private const val extraInfoDBURL = "jdbc:sqlite:./extra_info.db"
    private const val plotColumn = "plot"
    private const val imageUrlColumn = "image_url"
    private const val createTableQuery = "create table if not exists info (id INTEGER PRIMARY KEY AUTOINCREMENT, title string, plot string, image_url string, source integer)"

    private fun openConnectionToExtraInfo() {
        try {
            connection = DriverManager.getConnection(extraInfoDBURL)
        } catch (e: SQLException) {
            println("Could not create connection $extraInfoDBURL $e")
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
            statement?.executeUpdate(createTableQuery)
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
            ("select * from info WHERE title = '$title'")

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
        val overview = getMovieColumn(title, plotColumn)
        closeConnectionToExtraInfo()
        return overview
    }

    @JvmStatic
    fun getImageUrl(title: String): String? {
        openConnectionToExtraInfo()
        val imageUrl = getMovieColumn(title, imageUrlColumn)
        closeConnectionToExtraInfo()
        return imageUrl
    }
}
