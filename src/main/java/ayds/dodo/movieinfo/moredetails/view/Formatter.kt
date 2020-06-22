package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

interface Formatter{
    fun getFormattedPlotText(movieData: TMDBMovie): String
}

internal class HTMLFormatter: Formatter {
    private val greaterThanSymbol = ">"
    private val doubleBackSlashLineBreak = "\\n"
    private val singleLineBreak = "\n"
    private val linkOpen = "<a href="
    private val linkClose = "</a>"
    private val hyperlinkText = "View Movie Poster"
    private val htmlClose = "</html>"
    private val bodyClose = "</body>"
    private val htmlOpen = "<html>"
    private val bodyOpen = "<body style=\"width: 400px\">"
    private val fontOpen = "<font face=\"arial\">"
    private val fontClose = "</font>"
    private val boldOpen = "<b>"
    private val boldClose = "</b>"

    override fun getFormattedPlotText(movieData: TMDBMovie): String {
        var formattedText = movieData.plot
        formattedText = replaceLineBreakMarks(formattedText)
        formattedText = textToHtml(formattedText)
        formattedText = highlightTitle(formattedText, movieData.title)
        formattedText += singleLineBreak
        formattedText += getPosterPathText(movieData.posterPath)
        formattedText += closeHTML()
        return formattedText
    }

    private fun replaceLineBreakMarks(text: String) =
            text.replace(doubleBackSlashLineBreak, singleLineBreak)

    private fun replaceQuotes(text: String) = text.replace("'", "''")

    private fun highlightTitle(text: String, title: String) =
            text.replace(title,
                    boldOpen + title + boldClose)

    private fun textToHtml(text: String): String {
        val builder = StringBuilder()
        builder.append(htmlOpen + bodyOpen)
                .append(fontOpen)
                .append(replaceQuotes(text))
                .append(fontClose)
        return builder.toString()
    }

    private fun getPosterPathText(posterPath: String) =
            linkOpen +
                    posterPath +
                    greaterThanSymbol +
                    hyperlinkText +
                    linkClose

    private fun closeHTML(): String =
            bodyClose + htmlClose
}