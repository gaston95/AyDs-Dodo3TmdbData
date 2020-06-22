package ayds.dodo.movieinfo.moredetails.view

import javax.swing.event.HyperlinkEvent

sealed class MoreDetailsUiEvent

class HyperLinkAction(val hyperLink: HyperlinkEvent): MoreDetailsUiEvent()