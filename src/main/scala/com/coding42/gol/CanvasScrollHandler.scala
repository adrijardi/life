package com.coding42.gol

import javafx.event.EventHandler

import scalafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent

import com.coding42.gol.GameOfLife.Pos

/**
  * Calculates the scroll position for the canvas on base of the mouse input
  */
class CanvasScrollHandler(canvas: Canvas, updateFn: (Pos) => Unit) {

  var dragStart: (Double, Double) = (0,0)

  canvas.onMousePressed_=(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit =
      if(event.isPrimaryButtonDown)
        dragStart = (event.getSceneX, event.getSceneY)
  })

  canvas.onMouseDragged_=(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      updateFn(
        Pos(
          (dragStart._1 - event.getSceneX).toInt,
          (dragStart._2 - event.getSceneY).toInt
        )
      )
      dragStart = (event.getSceneX, event.getSceneY)
    }
  })

}
