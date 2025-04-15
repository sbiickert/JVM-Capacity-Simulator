package ca.esri.capsim
package app.ui

import app.io.GraphicsLib
import app.io.GraphicsResolution.HIGH

import java.awt.{Color, Dimension, Image}
import scala.swing.{Component, Graphics2D}

class DesignView extends Component:
  this.background = Color.white
  preferredSize = Dimension(200,200)

  override def paintComponent(g: Graphics2D): Unit =
    g.setColor(Color.white)
    g.fillRect(0,0,bounds.width,bounds.height)
    g.setColor(Color.blue)
    g.drawString("abc", 20, 20)
    g.drawImage(GraphicsLib.readGraphic("AGOL"), null, 100, 50)
    val hqImg = GraphicsLib.readGraphic("AGOL", HIGH)
    g.drawImage(hqImg, null, 100, 100)
    g.drawImage(hqImg, 100, 150, hqImg.getWidth / 2, hqImg.getHeight / 2, null)
