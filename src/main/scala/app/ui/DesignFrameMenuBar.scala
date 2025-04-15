package ca.esri.capsim
package app.ui

import scala.swing.{Action, ButtonGroup, CheckMenuItem, Menu, MenuBar, MenuItem, RadioMenuItem, Separator}

class DesignFrameMenuBar() extends MenuBar:
  contents += new Menu("A Menu") {
    contents += new MenuItem("An item")
    contents += new MenuItem(Action("An action item") {
      println("Action 'something' invoked")
    })
    contents += new Separator
    contents += new CheckMenuItem("Check me")
    contents += new CheckMenuItem("Me too!")
    contents += new Separator
    val a = new RadioMenuItem("a")
    val b = new RadioMenuItem("b")
    val c = new RadioMenuItem("c")
    val mutex = new ButtonGroup(a, b, c)
    contents ++= mutex.buttons
  }
  contents += new Menu("Empty Menu")
end DesignFrameMenuBar

