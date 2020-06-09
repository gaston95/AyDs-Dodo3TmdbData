package ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.sqldb



import ayds.dodo.movieinfo.moredetails.fulllogic.model.entities.TMDBMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.model.repository.local.MoreDetailsLocalStorage
import java.sql.SQLException



internal class SqlDBImp (private val sqlQueries: SQLQueries) : SqlDB(), MoreDetailsLocalStorage {
        override val dbUrl = SQLQueries.EXTRAINFO_DB_URL

    init {
        initDatabase()
    }

    private fun initDatabase() {
        openConnectionToExtraInfo()
        createInfoTableIfNeeded()
        closeConnectionToExtraInfo()
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


    private fun insertMovieInfo(movie: TMDBMovie){
        try {
            statement?.executeUpdate(SQLQueries.getInsertMovieInfoQuery(movie))
        } catch (e: SQLException) {
            println("Error saving " + e.message)
        }
    }

    override fun saveMovieInfo(movie: TMDBMovie) {
        openConnectionToExtraInfo()
        insertMovieInfo(movie)
        closeConnectionToExtraInfo()
    }

    override fun getMovieInfo(title: String): TMDBMovie? {
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