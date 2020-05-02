package ayds.dodo.movieinfo.moredetails.fulllogic

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DataBase {
    @JvmStatic
    fun createNewDatabase() {
        val url = "jdbc:sqlite:./extra_info.db"
        try {
            DriverManager.getConnection(url).use { connection ->
                if (connection != null) {
                    val meta = connection.metaData
                    println("The driver name is " + meta.driverName)
                    println("A new database has been created.")
                    val statement = connection.createStatement()
                    statement.queryTimeout = 30 // set timeout to 30 sec.
                    statement.executeUpdate("create table info (id INTEGER PRIMARY KEY AUTOINCREMENT, title string, plot string, image_url string, source integer)")
                }
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    fun testDB() {
        var connection: Connection? = null
        try { // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")
            val statement = connection.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.
            //statement.executeUpdate("drop table if exists person");
//statement.executeUpdate("create table person (id integer, name string)");
//statement.executeUpdate("insert into person values(1, 'leo')");
//statement.executeUpdate("insert into person values(2, 'yui')");
            val rs = statement.executeQuery("select * from info")
            while (rs.next()) { // read the result set
                println("id = " + rs.getInt("id"))
                println("title = " + rs.getString("title"))
                println("source = " + rs.getString("source"))
            }
        } catch (e: SQLException) { // if the error message is "out of memory",
// it probably means no database file is found
            System.err.println(e.message)
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) { // connection close failed.
                System.err.println(e)
            }
        }
    }

    @JvmStatic
    fun saveMovieInfo(title: String, plot: String, imageUrl: String) {
        var connection: Connection? = null
        try { // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")
            val statement = connection.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.
            println("INSERT  $title', '$plot', '$plot")
            statement.executeUpdate("insert into info values(null, '$title', '$plot', '$imageUrl', 1)")
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
        try { // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")
            val statement = connection.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.
            val rs = statement.executeQuery("select * from info WHERE title = '$title'")
            rs.next()
            return rs.getString("plot")
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
            connection = DriverManager.getConnection("jdbc:sqlite:./extra_info.db")
            val statement = connection.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.
            val rs = statement.executeQuery("select * from info WHERE title = '$title'")
            rs.next()
            return rs.getString("image_url")
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
}