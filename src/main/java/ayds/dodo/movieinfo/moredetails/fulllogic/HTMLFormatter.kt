package ayds.dodo.movieinfo.moredetails.fulllogic

object HTMLFormatter {
    fun getFormattedPlotText(movieData: OtherInfoData): String {
        var formattedText = movieData.getText()
        formattedText = replaceLineBreakMarks(formattedText)
        formattedText = textToHtml(formattedText)
        formattedText = highlightTitle(formattedText, movieData.getTitle())
        formattedText += HTMLTags.singleLineBreak
        formattedText += getPosterPathText(movieData)
        formattedText += closeHTML()
        return formattedText
    }

    private fun replaceLineBreakMarks(text: String) =
            text.replace(HTMLTags.doubleBackSlashLineBreak, HTMLTags.singleLineBreak)

    private fun replaceQuotes(text: String) = text.replace("'", "''")

    private fun highlightTitle(text: String, title: String) =
            text.replace(title,
                    HTMLTags.boldOpen + title + HTMLTags.boldClose)

    private fun textToHtml(text: String): String {
        val builder = StringBuilder()
        builder.append(HTMLTags.htmlOpen + HTMLTags.bodyOpen)
                .append(HTMLTags.fontOpen)
                .append(replaceQuotes(text))
                .append(HTMLTags.fontClose)
        return builder.toString()
    }

    private fun getPosterPathText(movieData: OtherInfoData) =
            HTMLTags.linkOpen +
                    movieData.getPosterPath() +
                    HTMLTags.greaterThanSymbol +
                    HTMLTags.hyperlinkText +
                    HTMLTags.linkClose

    private fun closeHTML(): String =
            HTMLTags.bodyClose + HTMLTags.htmlClose
}