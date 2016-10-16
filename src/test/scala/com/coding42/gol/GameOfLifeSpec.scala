package com.coding42.gol

import org.scalatest.{FlatSpec, Matchers}

class GameOfLifeSpec extends FlatSpec with Matchers {

  it should "load the block" in {
    val block = Vector(
      Vector(false, false, false, false),
      Vector(false, true, true, false),
      Vector(false, true, true, false),
      Vector(false, false, false, false)
    )

    GameOfLife(block).board shouldBe block
  }

  it should "not change the block" in {
    val block = Vector(
      Vector(false, false, false, false),
      Vector(false, true, true, false),
      Vector(false, true, true, false),
      Vector(false, false, false, false)
    )

    val life: GameOfLife = GameOfLife(block).getNextStep
    life.board shouldBe block
  }

  it should "change the blinker" in {
    val blinker = Vector(
      Vector(false, false, false, false, false),
      Vector(false, false, false, false, false),
      Vector(false, true, true, true, false),
      Vector(false, false, false, false, false),
      Vector(false, false, false, false, false)
    )

    val blinkerMoved = Vector(
      Vector(false, false, false, false, false),
      Vector(false, false, true, false, false),
      Vector(false, false, true, false, false),
      Vector(false, false, true, false, false),
      Vector(false, false, false, false, false)
    )

    val life: GameOfLife = GameOfLife(blinker).getNextStep
    life.board shouldBe blinkerMoved
    life.getNextStep.board shouldBe blinker
  }

  it should "generate a random array of the specified size" in {
    val seed = 1001
    val expected = Vector(
      Vector(true, true),
      Vector(true, false),
      Vector(true, true)
    )

    GameOfLife(3, 2, seed).board shouldBe expected
  }

}
