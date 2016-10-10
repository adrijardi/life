package com.coding42.gol

import com.coding42.gol.GameOfLife.{GameConfig, LifeBoard}

import scala.util.Random

class GameOfLife(arrays: Array[LifeBoard])(implicit conf: GameConfig) {
  import GameOfLife._

  private var currentArray = 0

  private def currentStep: LifeBoard = arrays(currentArray)

  private def nextArrayIdx = (currentArray +1) % arrays.length
  private def nextStep: LifeBoard = arrays(nextArrayIdx)

  private def countNeighbors(pos: Pos) = {
    pos.neighbours.count(currentStep.isAlive)
  }

  private def evaluatePos(pos: Pos): Boolean =
    (currentStep.isAlive(pos), countNeighbors(pos)) match {
      case (true, 0 | 1)  => false  // Any live cell with fewer than two live neighbours dies, as if caused by under-population.
      case (true, 2 | 3)  => true   // Any live cell with two or three live neighbours lives on to the next generation.
      case (true, _)      => false  // Any live cell with more than three live neighbours dies, as if by over-population.
      case (false, 3)     => true   // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
      case (false, _)     => false
    }

  private def evaluateRow(x: Int, nextStepRow: Row): Unit = {
    (0 until conf.sizeY).foreach { y =>
      nextStepRow(y) = evaluatePos(Pos(x, y))
    }
  }

  def evaluateStep() = { // TODO use free monads
    (0 until conf.sizeX).foreach { x =>
      evaluateRow(x, nextStep(x))
    }
    currentArray = nextArrayIdx
  }

  def currentStatus = arrays(currentArray)
}

object GameOfLife {

  type Row = Array[Boolean]
  type LifeBoard = Array[Row]

  val numArrays = 2

  def apply(width: Int, height: Int, seed: Int = Random.nextInt): GameOfLife = {
    val randomGen = new Random(seed)

    val board: LifeBoard =
      Array {
        (0 until width).map{ _ =>
          Array(
            (0 until height).map( _ => randomGen.nextBoolean() ): _*)
        }: _*
      }

    val arrays: Array[LifeBoard] = Array.ofDim[Boolean](numArrays, width, height)
    arrays.update(0, board)
    new GameOfLife(arrays)(GameConfig(width, height))
  }

  def apply(start: LifeBoard): GameOfLife = {
    val sizeX: Int = start.length
    val sizeY: Int = start(0).length
    val arrays: Array[LifeBoard] = Array.ofDim[Boolean](numArrays, sizeX, sizeY)
    arrays.update(0, start)
    new GameOfLife(arrays)(GameConfig(sizeX, sizeY))
  }

  implicit class LifeBoardOps(val board: LifeBoard) {
    def isAlive(pos: Pos): Boolean = board(pos.x)(pos.y)
  }

  case class GameConfig(sizeX: Int, sizeY: Int)

  case class Pos(x: Int, y: Int) {
    def neighbours(implicit conf: GameConfig): Seq[Pos] = {
      for{
        newX <- x-1 to x+1 if newX >= 0 && newX < conf.sizeX
        newY <- y-1 to y+1 if newY >= 0 && newY < conf.sizeY && (newX != x || newY != y)
      } yield Pos(newX, newY)
    }
  }

}
