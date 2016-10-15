package com.coding42.gol

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.{Eventually, IntegrationPatience}

import scalafx.application.Platform

class LifeAppSpec extends FlatSpec with Eventually with IntegrationPatience with Matchers {

  it should "open the app and create a default canvas of 300x200" in {
    runApp()
    eventually {
      LifeApp.canvas.width.value shouldBe 300
      LifeApp.canvas.height.value shouldBe 200
    }
  }

  it should "check app updates steps" in {
    eventually {
      step shouldBe "Step: 1"
    }

    eventually {
      step shouldBe "Step: 2"
    }
  }

  it should "restart button does start steps again an can change size" in {
    eventually {
      step shouldBe "Step: 3"
    }

    Platform.runLater(LifeApp.restartBtn.fire())
    Platform.runLater(LifeApp.widthField.text = "100")
    Platform.runLater(LifeApp.heightField.text = "50")
    Platform.runLater(LifeApp.okBtn.fire())

    eventually {
      step shouldBe "Step: 1"
      LifeApp.canvas.width.value shouldBe 100
      LifeApp.canvas.height.value shouldBe 50
    }
  }

  private def step = LifeApp.stepText.text.value

  private def runApp(): Unit = {
    val thread = new Thread {
      override def run(): Unit = {
        LifeApp.main(Array.empty)
      }
    }
    thread.start()
  }

}
