package ayds.dodo.movieinfo.moredetails.fulllogic

import java.sql.*

object DataBase {
    private lateinit var connection: Connection

    private const val extraInfoDBURL = "jdbc:sqlite:./extra_info.db"
    private const val plot = "plot"
    private const val imageUrl = "image_url"
    private const val createTableQuery = "create table if not exists info (id INTEGER PRIMARY KEY AUTOINCREMENT, title string, plot string, image_url string, source integer)"
    private const val singleQuote = "'"
    private const val doubleQuote = "''"

    @JvmStatic
    fun createNewDatabase() {
        try {
            getConnectionToExtraInfo().use {
                it.initializeStatement().createTable()
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    @JvmStatic
    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        try{
            getConnectionToExtraInfo().use {
                it.initializeStatement().executeUpdate(saveMovieStringBuilder(title, plot, imageUrl))
            }
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @JvmStatic
    fun getOverview(title: String): String? {
        try{
            getConnectionToExtraInfo().use{
                return it.initializeTitleResultSet(title).getPlot()
            }
        } catch (e: Exception){
            System.err.println(e)
        }
        return null
    }

    @JvmStatic
    fun getImageUrl(title: String): String? {
        try{
            getConnectionToExtraInfo().use{
                return it.initializeTitleResultSet(title).getImageURL()
            }
        } catch (e: Exception){
            System.err.println(e)
        }
        return null
    }

    private fun getConnectionToExtraInfo(): Connection = DriverManager.getConnection(extraInfoDBURL)

    private fun Connection.initializeStatement(): Statement {
        val statement = this.createStatement()
        statement.queryTimeout = 30
        return statement
    }

    private fun Connection.initializeTitleResultSet(title: String) =
            this.initializeStatement().getTitleResultSet(title)

    private fun Statement.getTitleResultSet(title: String): ResultSet =
        this.executeQuery("select * from info WHERE title = '$title'")

    private fun ResultSet.getPlot(): String? {
        return if(!this.isClosed) {
            this.next()
            this.getString(plot)
        } else
            null
    }

    private fun ResultSet.getImageURL(): String? {
        return if(!this.isClosed) {
            this.next()
            this.getString(imageUrl)
        } else
            null
    }

    private fun saveMovieStringBuilder(title:String, plot: String, imageUrl: String): String =
            "insert into info values(null, '${title.replaceQuotes()}', '${plot.replaceQuotes()}', '$imageUrl', 1)"

    private fun String.replaceQuotes() = this.replace(singleQuote, doubleQuote)

    private fun Statement.createTable(): Int =
            this.executeUpdate(createTableQuery)

    //---------------------------------------------------------------------
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

    private fun isInfotableCreated(): Boolean {
        try {
            val tables = connection.metaData?.getTables(null, null, "info", null)
            return tables?.next() ?: false
        } catch (e: SQLException) {
        }
        return false
    }
}
