package ca.esri.capsim
package app.ui

import scala.swing.event.Key
import scala.swing.{Action, ButtonGroup, CheckMenuItem, Menu, MenuBar, MenuItem, RadioMenuItem, Separator}

class DesignFrameMenuBar() extends MenuBar:
  contents += fileMenu
  contents += editMenu
  contents += workflowMenu
  contents += helpMenu

//  contents += new Menu("A Menu") {
//    contents += new MenuItem("An item")
//    contents += new MenuItem(Action("An action item") {
//      println("Action 'something' invoked")
//    })
//    contents += new Separator
//    contents += new CheckMenuItem("Check me")
//    contents += new CheckMenuItem("Me too!")
//    contents += new Separator
//    val a = new RadioMenuItem("a")
//    val b = new RadioMenuItem("b")
//    val c = new RadioMenuItem("c")
//    val mutex = new ButtonGroup(a, b, c)
//    contents ++= mutex.buttons
//  }

  def fileMenu:Menu =
    new Menu("File") {
      contents += menuItem("new")
      contents += new MenuItem("Open")
      contents += new Menu("Recent") {
        contents += new MenuItem("File 1")
        contents += new MenuItem("File 2")
      }
      contents += new MenuItem("Close")
      contents += new MenuItem("Close Others")
    }

  def menuItem(key: String): MenuItem =
    key match
      case "new" => {
        val m = new MenuItem("New")
        m
      }


  def editMenu:Menu =
    new Menu("Edit") {}

  def workflowMenu: Menu =
    new Menu("Workflow") {}

  def helpMenu: Menu =
    new Menu("Help") {}
end DesignFrameMenuBar

