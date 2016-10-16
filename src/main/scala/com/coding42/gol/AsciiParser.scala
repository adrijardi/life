package com.coding42.gol

import com.coding42.gol.GameOfLife.LifeBoard

import scala.util.Try

object AsciiParser {

  def parse(str: String): Try[LifeBoard] = {
    val lines = str.lines

    Try(
      lines.map { line =>
        line.map {
          case 'O' | 'o' => true
          case '-' => false
        }.toVector
      }.toVector
    )
  }

}
