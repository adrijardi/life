package com.coding42.gol

import javafx.event.{ActionEvent, EventHandler}

import com.coding42.gol.SimulationManager.SimulationConfig

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Slider, TextField}
import scalafx.scene.layout._
import scalafx.scene.text.Text
import concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import scalafx.stage.{Modality, Stage}

object LifeApp extends JFXApp {

  val canvas = new Canvas(580, 300)
  val gc = canvas.graphicsContext2D

  val slider = new Slider()
  slider.setMin(1)
  slider.setMax(100)
  slider.setValue(1)
  slider.setShowTickLabels(true)
  slider.setShowTickMarks(true)
  slider.setMajorTickUnit(50)
  slider.setMinorTickCount(5)
  slider.setBlockIncrement(10)

  val restartBtn = new Button("Restart")

  val widthField = new TextField
  widthField.text = "300"
  val heightField = new TextField
  heightField.text = "200"

  val stepText = new Text("0")

  val down = new HBox()
//  down.setStyle("-fx-background-color: #FF0000;")
  down.minWidth = 400
  down.minHeight = 300
  down.children_=(List(
    stepText,
    restartBtn,
    slider
  ))

  stage = new JFXApp.PrimaryStage {
    title.value = "Life"
    width = 800
    height = 500
    scene = new Scene {
      content = new BorderPane {
        center = canvas
        bottom = down
      }
    }
  }

  val simulator = SimulationManager(slider, restartBtn, stepText, gc)
  var currentSim: SimulationManager = _
  restartSimulation()

  def restartSimulation() = {
    val seed = None
    val width = Try(widthField.text.value.toInt).getOrElse(300) // TODO check field has only numbers
    val height = Try(heightField.text.value.toInt).getOrElse(200)
    currentSim = simulator(SimulationConfig(seed, width, height))
    currentSim.runSimulation()
  }

  restartBtn.onAction = new EventHandler[ActionEvent] {

    override def handle(event: ActionEvent): Unit = {
      val dialog = new Stage()
      dialog.initModality(Modality.ApplicationModal)
      dialog.initOwner(stage)
      val dialogVbox = new VBox(20)

      val okBtn = new Button("OK")
      okBtn.onAction = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          dialog.close()
          currentSim.stop().onSuccess { case _ =>
            restartSimulation()
          }
        }
      }

      dialogVbox.children_= (List(
        new Text("Width: "),
        widthField,
        new Text("Height: "),
        heightField,
        okBtn
      ))

      val dialogScene = new Scene(dialogVbox, 300, 200)
      dialog.setScene(dialogScene)
      dialog.show()

    }
  }
}
