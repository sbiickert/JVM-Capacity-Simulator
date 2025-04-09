package ca.esri.capsim
package app

import ca.esri.capsim.app.ui.DesignFrame

import scala.swing.MenuBar.NoMenuBar.contents
import scala.swing.event.{ButtonClicked, WindowClosed, WindowClosing}
import scala.swing.{Button, Frame, SimpleSwingApplication, SwingApplication, Window}

class CapSimApp extends SwingApplication:
  var frames: List[Frame] = List.empty

  override def startup(args: Array[String]): Unit =
    setupReactions()

  private def setupReactions(): Unit =
    app.reactions += {
      case WindowClosing(source: Window) => {
        // Handle stuff here

        frames = frames.filterNot(f => {f == source})
        if frames.isEmpty then
          app.quit()
      }
    }

  def addFrame(): Frame =
    val frame = DesignFrame()
    frame.visible = true
    frames = frame +: frames
    app.listenTo(frame)
    frame

end CapSimApp

val app:CapSimApp = CapSimApp()

@main def main(args: String*) =
  app.startup(args.toArray)
  app.addFrame()
  app.addFrame()

