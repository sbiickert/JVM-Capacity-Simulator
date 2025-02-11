package ca.esri.capsim
package app

import scala.swing.MenuBar.NoMenuBar.contents
import scala.swing.{Button, Frame, MainFrame, SimpleSwingApplication}

@main def main() =
  val frame = MainFrame()
  frame.title = "Hello, world!"
  frame.contents = Button("Click Me")({})
  frame.visible = true

