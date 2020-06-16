package ayds.dodo.movieinfo.moredetails.view

import ayds.dodo.movieinfo.moredetails.model.entities.TMDBMovie

object HTMLFormatter {
    const val greaterThanSymbol = ">"
    const val doubleBackSlashLineBreak = "\\n"
    const val singleLineBreak = "\n"
    const val linkOpen = "<a href="
    const val linkClose = "</a>"
    const val hyperlinkText = "View Movie Poster"
    const val htmlClose = "</html>"
    const val bodyClose = "</body>"
    const val htmlOpen = "<html>"
    const val bodyOpen = "<body style=\"width: 400px\">"
    const val fontOpen = "<font face=\"arial\">"
    const val fontClose = "</font>"
    const val boldOpen = "<b>"
    const val boldClose = "</b>"

    fun getFormattedPlotText(movieData: TMDBMovie): String {
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