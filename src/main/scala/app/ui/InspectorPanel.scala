package ca.esri.capsim
package app.ui

import java.awt.Dimension
import scala.swing.{Alignment, GridBagPanel, Label}
import scala.swing.GridBagPanel.{Anchor, Fill}

class InspectorPanel extends GridBagPanel:
  val c = Constraints()
  c.fill = Fill.Both
  c.weightx = 0.5
  c.weighty = 0.5

  var titleText = Label("Title")
  titleText.xAlignment = Alignment.Leading
  c.anchor = Anchor.LineStart
  c.gridx = 0
  c.gridy = 0
  layout(titleText) = c

  c.anchor = Anchor.LineStart

  minimumSize = Dimension(250,250)
end InspectorPanel

