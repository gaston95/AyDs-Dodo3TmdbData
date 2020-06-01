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
            HTMLTags.linkOpen +
            movieData.getPosterPath() +
            HTMLTags.greaterThanSymbol +
            HTMLTags.hyperlinkText +
            HTMLTags.linkClose

    private fun closeHTML(): String =
            HTMLTags.bodyClose + HTMLTags.htmlClose

    private fun getFormattedPlotText(movieData: OtherInfoData): String {
        var formattedText = movieData.getText()
        formattedText = replaceQuotationMarks(formattedText)
        formattedText = textToHtml(formattedText)
        formattedText += HTMLTags.singleLineBreak
        formattedText += getPosterPathText(movieData)
        formattedText += closeHTML()
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

    private fun textToHtml(text: String): String {
        val builder = StringBuilder()
        builder.append(HTMLTags.htmlOpen + HTMLTags.bodyOpen)
                .append(HTMLTags.fontOpen)
        val textWithReplacedQuotes = replaceQuotes(text)
        builder.append(highlightTitle(textWithReplacedQuotes))
                .append(HTMLTags.fontClose)
        return builder.toString()
    }

    private fun replaceQuotes(text: String) = text.replace("'", "''")

    private fun highlightTitle(text: String) =
            text.replace( movie.title,
                    HTMLTags.boldOpen + movie.title + HTMLTags.boldClose)

    private fun replaceQuotationMarks(text: String) =
            text.replace(HTMLTags.doubleBackSlashLineBreak, HTMLTags.singleLineBreak)
}