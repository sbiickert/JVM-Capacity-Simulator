package ca.esri.capsim
package app.ui

import java.awt.{Font, Insets}
import scala.swing.Font.Style
import scala.swing.{Alignment, GridBagPanel, Label}
import scala.swing.GridBagPanel.{Anchor, Fill}

class DesignStatusBar extends GridBagPanel:
  val c = Constraints()
  c.fill = Fill.Horizontal
  c.weightx = 1.0

  var leadingText = Label("Leading")
  leadingText.xAlignment = Alignment.Leading
  c.anchor = Anchor.LineStart
  c.gridx = 0
  c.gridy = 0
  c.insets = Insets(4, 12, 4, 4)
  leadingText.font = DesignStatusBar.displayFont
  layout(leadingText) = c

  var centeredText = Label(("Centered"))
  c.anchor = Anchor.Center
  c.gridx = 1
  c.gridy = 0
  c.insets = Insets(4, 12, 4, 12)
  centeredText.font = DesignStatusBar.displayFont
  layout(centeredText) = c

  var trailingText = Label("Trailing")
  trailingText.xAlignment = Alignment.Trailing
  c.anchor = Anchor.LineEnd
  c.gridx = 2
  c.gridy = 0
  c.insets = Insets(4, 4, 4, 12)
  trailingText.font = DesignStatusBar.displayFont
  layout(trailingText) = c

end DesignStatusBar

object DesignStatusBar:
  lazy val displayFont: Font =
    Font(Font.MONOSPACED, Font.PLAIN, 10)
end DesignStatusBar
