package com.coding42.gol

import org.scalatest.{FlatSpec, Matchers}

class AsciiParserSpec extends FlatSpec with Matchers {

  it should "parse the block" in {
    val expected = Array(
      Array(false, false, false, false),
      Array(false, true, true, false),
      Array(false, true, true, false),
      Array(false, false, false, false)
    )
    AsciiParser.parse(Patterns.block).get shouldBe expected
  }

  it should "parse the beehive" in {
    val expected = Array(
      Array(false, false, false, false, false, false),
      Array(false, false, true, true, false, false),
      Array(false, true, false, false, true, false),
      Array(false, false, true, true, false, false),
      Array(false, false, false, false, false, false)
    )
    AsciiParser.parse(Patterns.beehive).get shouldBe expected
  }
}
