package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase.createNewDatabase
import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase.getImageUrl
import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase.getOverview
import ayds.dodo.movieinfo.moredetails.fulllogic.DataBase.saveMovieInfo
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class OtherInfoWindow {
    private var contentPane = JPanel()
    private var descriptionTextPane = JTextPane()
    private var imagePanel = JPanel()

    private val single_line_break = "\n"
    private val local_movie = "[*]"
    private val api_url = "https://api.themoviedb.org/3/"

    companion object {
        fun open(movie: OmdbMovie) {
            val win = createWindow()

            win.getMoviePlot(movie)
        }

        private val content_type = "text/html"
        private val frame_title = "Movie Info Dodo"
        private val label_text = "Data from The Movie Data Base"
        private val width = 600
        private val height = 400

        private fun createWindow(): OtherInfoWindow {
            val win = OtherInfoWindow()

            win.contentPane.layout = BoxLayout(win.contentPane, BoxLayout.PAGE_AXIS)
            win.contentPane.add(JLabel(label_text))

            win.contentPane.add(win.imagePanel)

            val descriptionPanel = JPanel(BorderLayout())
            win.descriptionTextPane.isEditable = false
            win.descriptionTextPane.contentType = content_type
            win.descriptionTextPane.maximumSize = Dimension(width, height)
            descriptionPanel.add(win.descriptionTextPane)
            win.contentPane.add(descriptionPanel)

            val frame = JFrame(frame_title)
            frame.minimumSize = Dimension(width, height)
            frame.contentPane = win.contentPane
            frame.pack()
            frame.isVisible = true

            return win
        }
    }

    private val path_default = "https://www.themoviedb.org/assets/2/v4/logos/" +
            "256x256-dark-bg-01a111196ed89d59b90c31440b0f77523e9d9a9acac04a7bac00c27c6ce511a9.png"
    private val path_url = "https://image.tmdb.org/t/p/w400/"
    private val link_open = "<a href="
    private val link_close = "</a>"
    private val hyperlink_text = "View Movie Poster"

    private fun getMoviePlot(movie: OmdbMovie) {
        setHyperLinkListener()

        Thread(Runnable {

            createNewDatabase()
            var text = getOverview(movie.title)
            var path = getImageUrl(movie.title)

            if (movieExistsInDb(text, path)) {
                text = getTextInDB(text)
            } else {
                text = "No Results"
                path = path_default

                val searchResult = searchMovie(movie)
                if (searchResult != null) {

                    val extract = searchResult["overview"]
                    if (isNotNull(extract)) {
                        text = extract.asString.replace("\\n", single_line_break)
                        text = textToHtml(text, movie.title)

                        val backdropPathJson = searchResult["backdrop_path"]
                        if (isNotNull(backdropPathJson))
                            path = path_url + backdropPathJson.asString

                        val posterPath = searchResult["poster_path"]
                        if (isNotNull(posterPath))
                            text += single_line_break + link_open + path_url + posterPath.asString +
                                ">" + hyperlink_text + link_close

                        saveMovieInfo(movie.title, text, path)
                    }
                }
            }
            descriptionTextPane.text = text
            setImage(path)
        }).start()
    }

    private fun setHyperLinkListener() {
        descriptionTextPane.addHyperlinkListener { e: HyperlinkEvent ->
            if (HyperlinkEvent.EventType.ACTIVATED == e.eventType) {
                val desktop = Desktop.getDesktop()
                try {
                    desktop.browse(e.url.toURI())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun movieExistsInDb(text: String?, path: String?): Boolean = text != null && path != null

    private fun getTextInDB(text: String?): String {
        return local_movie + text
    }

    private fun isNotNull(element: JsonElement?): Boolean = element != null && !element.isJsonNull


    private fun searchMovie(movie: OmdbMovie): JsonObject? {
        val tmdbAPI = createAPI()
        try {
            val callResponse = tmdbAPI.getTerm(movie.title)?.execute()
            val gson = Gson()
            val jobj = gson.fromJson(callResponse?.body(), JsonObject::class.java)
            val resultIterator: Iterator<JsonElement> = jobj["results"].asJsonArray.iterator()
            var result: JsonObject
            while (resultIterator.hasNext()) {
                result = resultIterator.next().asJsonObject
                if (areSameYear(result, movie.year)) {
                    return result
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createAPI(): TheMovieDBAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(api_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        return retrofit.create(TheMovieDBAPI::class.java)
    }

    private fun areSameYear(result: JsonObject, movieYear: String): Boolean {
        val yearJson = result["release_date"]
        val year = yearJson?.asString?.split("-")?.toTypedArray()?.get(0) ?: ""
        return year == movieYear
    }

    private fun setImage(path: String?) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ignored: Exception) {
        }
        try {
            val url = URL(path)
            val image = ImageIO.read(url)
            val label = JLabel(ImageIcon(image))
            imagePanel.add(label)
            contentPane.validate()
            contentPane.repaint()
        } catch (exp: Exception) {
            exp.printStackTrace()
        }
    }

    private val html_open = "<html>"
    private val body_open = "<body style=\"width: 400px\">"
    private val font_open = "<font face=\"arial\">"
    private val font_close = "</font>"
    private val bold_open = "<b>"
    private val bold_close = "</b>"

    private fun textToHtml(text: String, term: String): String {
        val builder = StringBuilder()
        builder.append(html_open + body_open)
                .append(font_open)
        val textWithReplacedQuotes = replaceQuotes(text)
        builder.append(makeTermBold(textWithReplacedQuotes, term))
                .append(font_close)
        return builder.toString()
    }

    private fun replaceQuotes(text: String): String {
        return text.replace("'", "`")
    }

    private fun makeTermBold(text: String, term: String): String {
        return text.replace("(?i)" + term.toRegex(), bold_open + term.toUpperCase() + bold_close)
    }
}