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
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}

object LifeApp extends JFXApp {

  val startWidth = 300
  val startHeight = 200
  val canvas = new Canvas(startWidth, startHeight)
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
  val okBtn = new Button("OK")

  val widthField = new TextField
  widthField.text = ""+startWidth
  val heightField = new TextField
  heightField.text = ""+startHeight

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

  private val speedProvider = () => slider.value.value.toInt
  private val stepUpdater = (step: Int) => stepText.text = "Step: " + step
  private val imageUpdater = (image: Image) => gc.drawImage(image, 0, 0)

  val generator = SimulationManager.generator(speedProvider, stepUpdater, imageUpdater)
  var currentSim: SimulationManager = _
  restartSimulation()

  def restartSimulation() = {
    val seed = None
    val width = Try(widthField.text.value.toInt).getOrElse(startWidth) // TODO check field has only numbers
    val height = Try(heightField.text.value.toInt).getOrElse(startHeight)
    canvas.width_=(width)
    canvas.height_=(height)
    currentSim = generator(SimulationConfig(seed, width, height))
    currentSim.start()
  }

  restartBtn.onAction = new EventHandler[ActionEvent] {

    override def handle(event: ActionEvent): Unit = {
      val dialog = new Stage()
      dialog.initModality(Modality.ApplicationModal)
      dialog.initOwner(stage)
      val dialogVbox = new VBox(20)

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
