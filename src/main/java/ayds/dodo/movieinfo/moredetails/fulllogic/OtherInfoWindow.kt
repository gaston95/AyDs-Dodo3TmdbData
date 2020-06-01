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
    private val greaterThanSymbol = ">"
    private val doubleBackSlashLineBreak = "\\n"
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

    private fun getPosterPathText(movieData: OtherInfoData) =
            linkOpen + movieData.getPosterPath() + greaterThanSymbol + hyperlinkText + linkClose


    private fun getFormattedPlotText(movieData: OtherInfoData): String {
        var formattedText = movieData.getText()
        formattedText = formattedText.replace(doubleBackSlashLineBreak, singleLineBreak)
        formattedText = textToHtml(formattedText)
        formattedText += singleLineBreak
        formattedText += getPosterPathText(movieData)
        return formattedText
    }

    private fun setDescriptionTextPane(text: String){
        descriptionTextPane.text = text
    }

    private fun getMoviePlot() {
        Thread(Runnable {
            val movieData = OtherInfoData(movie)
            setDescriptionTextPane(getFormattedPlotText(movieData))
            setImage(movieData.getImageURL())
            setLookAndFeel()
        }).start()
    }

    private fun setHyperLinkListener() {
        descriptionTextPane.addHyperlinkListener { e: HyperlinkEvent ->
            if (HyperlinkEvent.EventType.ACTIVATED == e.eventType) {
                val desktop = Desktop.getDesktop()
                try {
                    desktop.browse(e.url.toURI())
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
    }

    private fun setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getImageIcon(path: String?): JLabel{
        val url = URL(path)
        val image = ImageIO.read(url)
        return JLabel(ImageIcon(image))
    }

    private fun setImage(path: String?) {
        try {
            imagePanel.add(getImageIcon(path))
            contentPane.validate()
            contentPane.repaint()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private val htmlOpen = "<html>"
    private val bodyOpen = "<body style=\"width: 400px\">"
    private val fontOpen = "<font face=\"arial\">"
    private val fontClose = "</font>"
    private val boldOpen = "<b>"
    private val boldClose = "</b>"

    private fun textToHtml(text: String): String {
        val builder = StringBuilder()
        builder.append(htmlOpen + bodyOpen)
                .append(fontOpen)
        val textWithReplacedQuotes = replaceQuotes(text)
        builder.append(highlightTitle(textWithReplacedQuotes))
                .append(fontClose)
        return builder.toString()
    }

    private val quoteSymbol = "'"
    private val doubleQuoteSymbols = "''"
    private val caseInsensitiveRegexSymbol = "(?i)"

    private fun replaceQuotes(text: String) = text.replace(quoteSymbol, doubleQuoteSymbols)

    private fun highlightTitle(text: String) =
            text.replace(caseInsensitiveRegexSymbol + movie.title.toRegex(),
                         boldOpen + movie.title.toUpperCase() + boldClose)
}