package com.coding42.gol

import java.util.concurrent.atomic.AtomicBoolean

import com.coding42.gol.GameOfLife.LifeBoard

import scala.concurrent.{Future, Promise}
import scalafx.scene.image.{Image, WritableImage}
import scalafx.scene.paint.Color
import scalaz.concurrent.Task

class SimulationManager(seed: Option[Int], width: Int, height: Int, speedProvider: () => Int, stepUpdater: Int => Unit,
                        imageUpdater: Image => Unit) {

  var running = new AtomicBoolean(false)
  val finishPromise = Promise[Unit]()

  var life = seed match {
    case Some(s) => GameOfLife(width, height, s)
    case _       => GameOfLife(width, height)
  }

  val task = Task.fork {
    var step = 0
    Task {
      while(running.get()) {
        life = life.getNextStep
        drawBoard(life.board)
        step += 1
        stepUpdater(step)
        Thread.sleep(calculateSleep)
      }
      finishPromise.success()
    }
  }

  def calculateSleep: Int = {
    1000 / speedProvider()
  }

  def start(): Boolean = {
    if(running.compareAndSet(false, true)) {
      task.unsafePerformAsync(_ => ())
      true
    } else {
      false
    }
  }

  def stop(): Future[Unit] = {
    running.set(false)
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

    imageUpdater(image)
  }

}

object SimulationManager {

  case class SimulationConfig(seed: Option[Int], width: Int, height: Int)

  type SimGenerator = SimulationConfig => SimulationManager

  def generator(speedProvider: () => Int, stepUpdater: Int => Unit, imageUpdater: Image => Unit): SimGenerator = {
    case SimulationConfig(seed, width, height) =>
      new SimulationManager(seed, width, height, speedProvider, stepUpdater, imageUpdater)
  }

}
