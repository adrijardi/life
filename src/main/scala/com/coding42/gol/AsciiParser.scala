package com.coding42.gol

import com.coding42.gol.GameOfLife.LifeBoard

import scala.util.Try

object AsciiParser {

  def parse(str: String): Try[LifeBoard] = {
    val lines = str.lines

    val cols = lines.map { line =>
      val array = Array(line.map {
        case 'O' | 'o' => true
        case '-' => false
      }: _*)
      array
    }.toSeq

    Try(
      Array(cols: _*)
    )
  }

}
