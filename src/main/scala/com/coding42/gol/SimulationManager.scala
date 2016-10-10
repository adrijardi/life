package com.coding42.gol

import com.coding42.gol.GameOfLife.LifeBoard

import scala.concurrent.{Future, Promise}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.{Button, Slider}
import scalafx.scene.image.WritableImage
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalaz.concurrent.Task

class SimulationManager(seed: Option[Int], width: Int, height: Int,
                        speedSlider: Slider, restartBtn: Button, stepText: Text, canvasCtx: GraphicsContext) {

  var running = true
  val finishPromise = Promise[Unit]()

  val life = seed match {
    case Some(s) => GameOfLife(width, height, s)
    case _       => GameOfLife(width, height)
  }

  val task = Task.fork {
    var step = 0
    Task {
      while(running) {
        life.evaluateStep()
        drawBoard(life.currentStatus)
        step += 1
        stepText.text = ""+step
        Thread.sleep(calculateSleep)
      }
      finishPromise.success()
    }
  }

  def calculateSleep: Int = {
    1000 / speedSlider.value.value.toInt
  }

  def runSimulation(): Unit = {
    task.unsafePerformAsync(_ => ())
  }

  def stop(): Future[Unit] = {
    running = false
    finishPromise.future
  }

  def drawBoard(lifeBoard: LifeBoard) = {
    val width = lifeBoard.length
    val height = lifeBoard(0).length // TODO maybe move to case class instead

    val image = new WritableImage(width, height)
    val pixelWriter = image.pixelWriter

    lifeBoard.zipWithIndex.foreach { case (row, x) =>
      row.zipWithIndex.foreach{ case (alive, y) =>
        val color = if( lifeBoard(x)(y) )
          Color.Black
        else
          Color.White
        pixelWriter.setColor(x, y, color)
      }
    }

    canvasCtx.drawImage(image, 0, 0)
  }

}

object SimulationManager {

  case class SimulationConfig(seed: Option[Int], width: Int, height: Int)

  type SimGenerator = SimulationConfig => SimulationManager

  def apply(speedSlider: Slider, restartBtn: Button, stepText: Text, canvasCtx: GraphicsContext): SimGenerator = {
    case SimulationConfig(seed, width, height) =>
      new SimulationManager(seed, width, height, speedSlider, restartBtn, stepText, canvasCtx)
  }

}
