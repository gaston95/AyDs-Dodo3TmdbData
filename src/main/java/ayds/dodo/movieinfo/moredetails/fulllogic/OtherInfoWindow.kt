package ayds.dodo.movieinfo.moredetails.fulllogic

import ayds.dodo.movieinfo.home.model.entities.OmdbMovie
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.HyperlinkEvent

class OtherInfoWindow(private val movie: OmdbMovie) {
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

    private fun getMoviePlot() {
        Thread {
            val movieData = TMDBMovieResolver(movie).getMovie()
            if (movieData != null) {
                setDescriptionTextPane(movieData.plot)
                setImage(movieData.imageUrl)
            }
            setLookAndFeel()
        }.start()
    }

    private fun setDescriptionTextPane(text: String){
        descriptionTextPane.text = text
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

    private fun getImageIcon(path: String?): JLabel{
        val url = URL(path)
        val image = ImageIO.read(url)
        return JLabel(ImageIcon(image))
    }

    private fun setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}