package ayds.dodo.movieinfo.moredetails.fulllogic

import java.sql.*

object DataBase {

    @JvmStatic
    fun createNewDatabase() {

        try {
            getConnectionToExtraInfo().use { connection ->
                //val meta = connection.metaData
                //println("The driver name is " + meta.driverName)
                //println("A new database has been created.")
                val statement = connection.initializeStatement()
                statement.createTable() }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    @JvmStatic
    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        try{
            getConnectionToExtraInfo().use {
                it.initializeStatement().executeUpdate(getMovieStringToSave(title, plot, imageUrl))
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

    private fun getConnectionToExtraInfo(): Connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")

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

    private fun getMovieStringToSave(title:String, plot: String, imageUrl: String): String =
            "insert into info values(null, '${title.replaceQuotes()}', '${plot.replaceQuotes()}', '$imageUrl', 1)"

    private fun String.replaceQuotes() = this.replace("'", "''")
}
    private fun Statement.createTable(): Int =
        this.executeUpdate("create table if not exists info (id INTEGER PRIMARY KEY AUTOINCREMENT, title string, plot string, image_url string, source integer)")