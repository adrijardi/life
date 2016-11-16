package com.coding42.gol

import com.coding42.gol.GameOfLife.{GameConfig, LifeBoard}

import scala.util.Random

class GameOfLife(val board: LifeBoard)(implicit conf: GameConfig) {
  import GameOfLife._

  private def countNeighbors(pos: Pos) = {
    pos.neighbours.count(board.isAlive)
  }

  private def evaluatePos(pos: Pos): Boolean =
    (board.isAlive(pos), countNeighbors(pos)) match {
      case (true, 0 | 1)  => false  // Any live cell with fewer than two live neighbours dies, as if caused by under-population.
      case (true, 2 | 3)  => true   // Any live cell with two or three live neighbours lives on to the next generation.
      case (true, _)      => false  // Any live cell with more than three live neighbours dies, as if by over-population.
      case (false, 3)     => true   // Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
      case (false, _)     => false
    }

  private def evaluateRow(x: Int): Row = {
    (0 until conf.sizeY).map { y =>
      evaluatePos(Pos(x, y))
    }.toVector
  }

  def getNextStep: GameOfLife = {
    val newStep: LifeBoard = (0 until conf.sizeX).par.map { x =>
      evaluateRow(x)
    }.toVector
    new GameOfLife(newStep)
  }

}

object GameOfLife {

  type Row = Vector[Boolean]
  type LifeBoard = Vector[Row]

  val numArrays = 2

  def apply(width: Int, height: Int, seed: Int = Random.nextInt): GameOfLife = {
    val randomGen = new Random(seed)

    val board: LifeBoard =
      (0 until width).map{ _ =>
          (0 until height).map( _ => randomGen.nextBoolean() ).toVector
      }.toVector

    new GameOfLife(board)(GameConfig(width, height))
  }

  def apply(start: LifeBoard): GameOfLife = {
    val sizeX: Int = start.length
    val sizeY: Int = start(0).length
    new GameOfLife(start)(GameConfig(sizeX, sizeY))
  }

  implicit class LifeBoardOps(val board: LifeBoard) {
    def isAlive(pos: Pos): Boolean = board(pos.x)(pos.y)
  }

  case class GameConfig(sizeX: Int, sizeY: Int)

  case class Pos(x: Int, y: Int) { // TODO move into it's own class and extract neighbours function to companion object
    def +(other: Pos) = Pos(x + other.x, y + other.y)

    def neighbours(implicit conf: GameConfig): Seq[Pos] = {
      for{
        newX <- x-1 to x+1 if newX >= 0 && newX < conf.sizeX
        newY <- y-1 to y+1 if newY >= 0 && newY < conf.sizeY && (newX != x || newY != y)
      } yield Pos(newX, newY)
    }
  }

}
