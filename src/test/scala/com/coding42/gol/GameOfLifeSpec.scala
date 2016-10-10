package com.coding42.gol

import org.scalatest.{FlatSpec, Matchers}

class GameOfLifeSpec extends FlatSpec with Matchers {

  it should "load the block" in {
    val block = Array(
      Array(false, false, false, false),
      Array(false, true, true, false),
      Array(false, true, true, false),
      Array(false, false, false, false)
    )

    GameOfLife(block).currentStatus shouldBe block
  }

  it should "not change the block" in {
    val block = Array(
      Array(false, false, false, false),
      Array(false, true, true, false),
      Array(false, true, true, false),
      Array(false, false, false, false)
    )

    val life: GameOfLife = GameOfLife(block)
    life.evaluateStep()
    life.currentStatus shouldBe block
  }

  it should "change the blinker" in {
    val blinker = Array(
      Array(false, false, false, false, false),
      Array(false, false, false, false, false),
      Array(false, true, true, true, false),
      Array(false, false, false, false, false),
      Array(false, false, false, false, false)
    )

    val blinkerMoved = Array(
      Array(false, false, false, false, false),
      Array(false, false, true, false, false),
      Array(false, false, true, false, false),
      Array(false, false, true, false, false),
      Array(false, false, false, false, false)
    )

    val life: GameOfLife = GameOfLife(blinker)
    life.evaluateStep()
    life.currentStatus shouldBe blinkerMoved
    life.evaluateStep()
    life.currentStatus shouldBe blinker
  }

  it should "generate a random array of the specified size" in {
    val seed = 1001
    val expected = Array(
      Array(true, true),
      Array(true, false),
      Array(true, true)
    )

    GameOfLife(3, 2, seed).currentStatus shouldBe expected
  }

}
