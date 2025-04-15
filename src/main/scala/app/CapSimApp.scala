package ca.esri.capsim
package app

import ca.esri.capsim.app.doc.DesignDoc
import ca.esri.capsim.app.ui.DesignFrame
import ca.esri.capsim.engine.Design

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
    val frame = DesignFrame(DesignDoc(Design.empty))
    frame.visible = true
    frames = frame +: frames
    app.listenTo(frame)
    frame

end CapSimApp

val app:CapSimApp = CapSimApp()

@main def main(args: String*) =
  app.startup(args.toArray)
  System.setProperty("apple.laf.useScreenMenuBar", "true")
  System.setProperty("apple.awt.application.name", "Capacity Simulator")
  app.addFrame()
  app.addFrame()

