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
  }
}
