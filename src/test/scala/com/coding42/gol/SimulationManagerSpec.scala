package com.coding42.gol

import com.coding42.gol.SimulationManager.SimulationConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

class SimulationManagerSpec extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  it should "start and stop a simulation without failing" in {
    val generator = SimulationManager.generator( () => 100, _ => (), _ => ())
    val simulation = generator(SimulationConfig(None, 50, 10))
    simulation.start() shouldBe true
    Await.result(simulation.stop(), 1.second)
  }

  it should "when a simulation is started the steps should get updated" in {
    val promise = Promise[List[Int]]()
    var steps: List[Int] = Nil

    val stepUpdater: (Int) => Unit = step => {
      steps = step :: steps

      if(steps.length == 3) {
          promise.success(steps)
      }
    }

    val generator = SimulationManager.generator(() => 100, stepUpdater, _ => ())
    val simulation = generator(SimulationConfig(None, 50, 10))
    simulation.start() shouldBe true

    promise.future.futureValue shouldBe List(3,2,1)
    Await.result(simulation.stop(), 1.second)
  }
}
