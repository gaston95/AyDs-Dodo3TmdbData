package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.sqldb


import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

abstract class SqlDB {
    private lateinit var connection: Connection
    protected abstract val dbUrl: String
    protected val statement: Statement?
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
    protected fun openConnectionToExtraInfo() {
        try {
            connection = DriverManager.getConnection(SQLQueries.EXTRAINFO_DB_URL)
        } catch (e: SQLException) {
            println("Could not create connection ${SQLQueries.EXTRAINFO_DB_URL} $e")
        }
    }
    protected fun closeConnectionToExtraInfo() {
        try {
            connection.close()
        } catch (e: SQLException) {
            System.err.println(e)
        }
    }
    protected fun isInfoTableCreated(): Boolean {
        try {
            val tables = connection.metaData?.getTables(null, null, "info", null)
            return tables?.next() ?: false
        } catch (e: SQLException) {
        }
        return false
    }

}