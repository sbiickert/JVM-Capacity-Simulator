package ca.esri.capsim
package app.io

import app.io.GraphicsResolution.LOW

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.io.Source

enum GraphicsResolution:
  case LOW
  case HIGH

  def fileNameInsertion: String =
    this match
      case LOW => ""
      case HIGH => "@2x"

end GraphicsResolution


class GraphicsLib:

end GraphicsLib

object GraphicsLib:
  private val sep = System.getProperty("file.separator")

  private def dataFolder: String =
    val components = List(System.getProperty("user.dir"), "graphics")
    components.mkString(sep)

  private def getFullPath(fileName: String): String =
    List(dataFolder, fileName).mkString(sep)

  def readGraphic(fileName: String, resolution: GraphicsResolution = LOW): BufferedImage =
    val filePath = getFullPath(List(fileName, resolution.fileNameInsertion, ".png").mkString)
    ImageIO.read(File(filePath))

end GraphicsLib

