package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class OtherInfoWindow(val movie: OmdbMovie) {
    private val contentType = "text/html"
    private val frameTitle = "Movie Info Dodo"
    private val labelText = "Data from The Movie Data Base"
    private val width = 600
    private val height = 400
    private val singleLineBreak = "\n"
    private val linkOpen = "<a href="
    private val linkClose = "</a>"
    private val hyperlinkText = "View Movie Poster"

    private var contentPane = JPanel()
    private var descriptionTextPane = JTextPane()
    private var imagePanel = JPanel()


    init {
        createWindow()
        initWindow()
    }

    private fun createWindow() {
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        contentPane.add(JLabel(labelText))

        contentPane.add(imagePanel)

        val descriptionPanel = JPanel(BorderLayout())
        descriptionTextPane.isEditable = false
        descriptionTextPane.contentType = contentType
        descriptionTextPane.maximumSize = Dimension(width, height)
        descriptionPanel.add(descriptionTextPane)
        contentPane.add(descriptionPanel)

        val frame = JFrame(frameTitle)
        frame.minimumSize = Dimension(width, height)
        frame.contentPane = contentPane
        frame.pack()
        frame.isVisible = true
    }

    private fun initWindow() {
        setHyperLinkListener()
        getMoviePlot()
    }

    private fun getMoviePlot() {
        Thread(Runnable {
            val movieData = OtherInfoData(movie)
            var text = movieData.getText().replace("\\n", singleLineBreak)
            val imageUrl = movieData.getImageURL()
            text = textToHtml(text, movie.title)
            text += singleLineBreak + linkOpen + movieData.getPosterPath() +
                   ">" + hyperlinkText + linkClose
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

    private val htmlOpen = "<html>"
    private val bodyOpen = "<body style=\"width: 400px\">"
    private val fontOpen = "<font face=\"arial\">"
    private val fontClose = "</font>"
    private val boldOpen = "<b>"
    private val boldClose = "</b>"

    private fun textToHtml(text: String, term: String): String {
        val builder = StringBuilder()
        builder.append(htmlOpen + bodyOpen)
                .append(fontOpen)
        val textWithReplacedQuotes = replaceQuotes(text)
        builder.append(makeTermBold(textWithReplacedQuotes, term))
                .append(fontClose)
        return builder.toString()
    }

    private fun replaceQuotes(text: String) = text.replace("'", "`")

    private fun makeTermBold(text: String, term: String) = text.replace("(?i)" + term.toRegex(), boldOpen + term.toUpperCase() + boldClose)

}