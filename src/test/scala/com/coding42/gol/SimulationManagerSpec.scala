package com.coding42.gol

import com.coding42.gol.SimulationManager.SimulationConfig
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class SimulationManagerSpec extends FlatSpec with Matchers {

  it should "start and stop a simulation without failing" in {
    val generator = SimulationManager.generator( () => 100, _ => (), _ => ())
    val simulation = generator(SimulationConfig(None, 50, 10))
    simulation.start() shouldBe true
    Await.result(simulation.stop(), 1.second)
  }
}
