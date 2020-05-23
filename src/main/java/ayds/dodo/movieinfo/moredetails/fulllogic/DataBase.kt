package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase.getPlot
import java.sql.*

object DataBase {

    @JvmStatic
    fun createNewDatabase() {

        try {
            getConnectionToExtraInfo().use { connection ->
                if (connection != null) {
                    val meta = connection.metaData
                    //println("The driver name is " + meta.driverName)
                    //println("A new database has been created.")
                    val statement = createStatement(connection)
                    statement.executeUpdate("create table if not exists info (id INTEGER PRIMARY KEY AUTOINCREMENT, title string, plot string, image_url string, source integer)")
                }
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }
    }

    @JvmStatic
    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        var connection: Connection? = null
        try { // create a database connection
            connection = getConnectionToExtraInfo()
            val statement = createStatement(connection)
            println("INSERT  $title, $plot, $imageUrl")
            val plotSql = plot.replace("'","''")
            statement.executeUpdate("insert into info values(null, '$title', '$plotSql', '$imageUrl', 1)")
        } catch (e: SQLException) {
            System.err.println("Error saving " + e.message)
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) { // connection close failed.
                System.err.println(e)
            }
        }
    }

    @JvmStatic
    fun getOverview(title: String): String? {
        var connection: Connection? = null
        try {
            connection = getConnectionToExtraInfo()
            val statement = createStatement(connection)
            val rs = statement.getTitleResultSet(title)
            return rs.getPlot()
        } catch (e: SQLException) { // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println("Get title error " + e.message)
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) { // connection close failed.
                System.err.println(e)
            }
        }
        return null
    }

    @JvmStatic
    fun getImageUrl(title: String): String? {
        var connection: Connection? = null
        try { // create a database connection
            connection = getConnectionToExtraInfo()
            val statement = createStatement(connection)
            val rs = statement.getTitleResultSet(title)
            return rs.getImageURL()
        } catch (e: SQLException) { // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println("Get image error " + e.message)
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) { // connection close failed.
                System.err.println(e)
            }
        }
        return null
    }

    private fun getConnectionToExtraInfo(): Connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")

    private fun createStatement(connection: Connection): Statement {
        val statement = connection.createStatement()
        statement.queryTimeout = 30
        return statement
    }

    private fun Statement.getTitleResultSet(title: String): ResultSet =
        this.executeQuery("select * from info WHERE title = '$title'")

    fun ResultSet.getPlot(): String? {
        return if(!this.isClosed) {
            this.next()
            this.getString("plot")
        } else
            null
    }

    private fun ResultSet.getImageURL(): String? {
        return if(!this.isClosed) {
            this.next()
            this.getString("image_url")
        } else
            null
    }
}