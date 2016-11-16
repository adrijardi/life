package com.coding42.gol

import com.coding42.gol.GameOfLife.{LifeBoard, Pos}

import scalafx.scene.canvas.Canvas
import scalafx.scene.image.WritableImage
import scalafx.scene.paint.Color

/**
  * Handles zoom, drag and canvas updates
  */
case class CanvasPainter(canvas: Canvas, lifeBoardOp: Option[LifeBoard], zoom: Int, offset: Pos) {

  val boardWidth = lifeBoardOp.map(_.length).getOrElse(0)
  val boardHeight = lifeBoardOp.map(_(0).length).getOrElse(0)

  val clampedOffset = clamp(offset, -boardWidth/2, boardWidth/2-1, -boardHeight/2, boardHeight/2-1)

  def withZoom(zoom: Int) = this.copy(zoom = zoom)

  def withBoard(lifeBoard: LifeBoard) = this.copy(lifeBoardOp = Some(lifeBoard))

  def moveOffset(movement: Pos) = {
    this.copy(offset = clampedOffset + movement)
  }

  def paintBoard() = {
    val image = new WritableImage(canvas.width.toInt, canvas.height.toInt)

    lifeBoardOp.foreach { lifeBoard =>
      val pixelWriter = image.pixelWriter

      val mapper = imageMapper(lifeBoard, offset, zoom)

      lifeBoard.zipWithIndex.foreach { case (row, x) =>
        row.zipWithIndex.foreach { case (alive, y) =>

          pixelWriter.setColor(x, y, mapper(x, y))
        }
      }
    }

    canvas.graphicsContext2D.drawImage(image, 0, 0)
  }

  private def imageMapper(lifeBoard: LifeBoard, offset: Pos, zoom: Int): (Int, Int) => Color = {
    val widthDiv2: Int = boardWidth / 2 + clampedOffset.x
    val heightDiv2: Int = boardHeight / 2 + clampedOffset.y

    (x, y) => {
      val newX = (x - widthDiv2) / zoom + widthDiv2
      val newY = (y - heightDiv2) / zoom + heightDiv2

      if( lifeBoard(newX)(newY) )
        Color.Black
      else
        Color.White
    }
  }

  def clamp(pos: Pos, minX: Int, maxX: Int, minY: Int, maxY: Int): Pos = {
    Pos(clamp(pos.x, minX, maxX), clamp(pos.y, minY, maxY))
  }

  def clamp(value: Int, min: Int, max: Int): Int = {
    Math.min(Math.max(value, min), max)
  }

}
