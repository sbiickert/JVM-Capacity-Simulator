package ca.esri.capsim
package app.ui

import scala.swing.{Button, Frame}

class DesignFrame extends Frame:
  title = "Hello, world!"
  private val b = Button("Click Me")({})
  contents = b

end DesignFrame

