package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

interface Formatter{
    fun getFormattedPlotText(movieData: TMDBMovie): String
}

internal class HTMLFormatter: Formatter {
    val greaterThanSymbol = ">"
    val doubleBackSlashLineBreak = "\\n"
    val singleLineBreak = "\n"
    val linkOpen = "<a href="
    val linkClose = "</a>"
    val hyperlinkText = "View Movie Poster"
    val htmlClose = "</html>"
    val bodyClose = "</body>"
    val htmlOpen = "<html>"
    val bodyOpen = "<body style=\"width: 400px\">"
    val fontOpen = "<font face=\"arial\">"
    val fontClose = "</font>"
    val boldOpen = "<b>"
    val boldClose = "</b>"

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