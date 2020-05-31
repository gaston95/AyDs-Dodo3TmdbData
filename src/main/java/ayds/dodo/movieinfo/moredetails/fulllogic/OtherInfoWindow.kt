package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class OtherInfoWindow {
    private var contentPane = JPanel()
    private var descriptionTextPane = JTextPane()
    private var imagePanel = JPanel()

    private val single_line_break = "\n"
    private val link_open = "<a href="
    private val link_close = "</a>"
    private val hyperlink_text = "View Movie Poster"

    companion object {
        fun open(movie: OmdbMovie) {
            val win = createWindow()

            win.initWindow(movie)
        }

        private const val content_type = "text/html"
        private const val frame_title = "Movie Info Dodo"
        private const val label_text = "Data from The Movie Data Base"
        private const val width = 600
        private const val height = 400

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

    private fun initWindow(movie: OmdbMovie) {
        setHyperLinkListener()
        getMoviePlot(movie)
    }

    private fun getMoviePlot(movie: OmdbMovie) {
        Thread(Runnable {
            val movieData = OtherInfoData(movie)
            var text = movieData.getText().replace("\\n", single_line_break)
            val imageUrl = movieData.getImageURL()
            println(imageUrl)
            text = textToHtml(text, movie.title)
            text += single_line_break + link_open + movieData.getPosterPath() +
                   ">" + hyperlink_text + link_close
            descriptionTextPane.text = text
            setLookAndFeel()
            setImage(imageUrl)
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

    private fun setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ignored: Exception) {
        }
    }

    private fun setImage(path: String?) {
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