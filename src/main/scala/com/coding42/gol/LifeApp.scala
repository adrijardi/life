package com.coding42.gol

import javafx.beans.Observable
import javafx.event.ActionEvent

import com.coding42.gol.GameOfLife.{LifeBoard, Pos}
import com.coding42.gol.SimulationManager.SimulationConfig

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Slider, TextField}
import scalafx.scene.layout._
import scalafx.scene.text.Text
import scala.util.Try
import scalafx.stage.{Modality, Stage}

object LifeApp extends JFXApp {

  val startWidth = 300
  val startHeight = 200
  val canvas = new Canvas(startWidth, startHeight)
  val gc = canvas.graphicsContext2D

  val speedLabel = new Text("Speed")
  val speedSlider = new Slider()
  speedSlider.setMin(1)
  speedSlider.setMax(30)
  speedSlider.setValue(5)

  val zoomLabel = new Text("Zoom")
  val zoomSlider = new Slider()
  zoomSlider.setMin(1)
  zoomSlider.setMax(10)
  zoomSlider.setValue(0)
  zoomSlider.value.addListener((observable: Observable) => updatePainter(canvasPainter.withZoom(zoomSlider.value.toInt)))

  val restartBtn = new Button("Restart")
  val okBtn = new Button("OK")

  val widthField = new TextField
  widthField.text = ""+startWidth
  val heightField = new TextField
  heightField.text = ""+startHeight

  val stepText = new Text("0")

  val down = new VBox(10,
    new HBox(10,
      stepText,
      restartBtn
    ),
    new HBox(10,
      speedLabel,
      speedSlider,
      zoomLabel,
      zoomSlider
    )
  )

  stage = new JFXApp.PrimaryStage {
    title.value = "Life"
    width = 800
    height = 500
    resizable = false
    scene = new Scene {
      content = new BorderPane {
        center = canvas
        bottom = down
      }
    }
  }

  private val speedProvider = () => speedSlider.value.value.toInt
  private val stepUpdater = (step: Int) => stepText.text = "Step: " + step

  var canvasPainter = CanvasPainter(canvas, None, zoomSlider.value.toInt, Pos(0,0))
  def updatePainter(op: => CanvasPainter) = synchronized {
    canvasPainter = op
  }

  val canvasScrollHandler = new CanvasScrollHandler(
    canvas,
    movement => updatePainter(canvasPainter.moveOffset(movement))
  )

  private val imageUpdater = (image: LifeBoard) => {
    updatePainter(canvasPainter.withBoard(image))
    canvasPainter.paintBoard()
  }

  val generator = SimulationManager.generator(speedProvider, stepUpdater, imageUpdater)
  var currentSim: SimulationManager = _
  restartSimulation(startWidth, startHeight)

  def restartSimulation(width: Int, height: Int) = {
    val seed = None
    canvas.width_=(width)
    canvas.height_=(height)
    stage.sizeToScene()
    currentSim = generator(SimulationConfig(seed, width, height))
    currentSim.start()
  }

  restartBtn.onAction = (event: ActionEvent) => {
    val dialog = new Stage()
    dialog.initModality(Modality.ApplicationModal)
    dialog.initOwner(stage)
    val errorMsg = new Text("")

    okBtn.onAction = (event: ActionEvent) => {
      val a: Try[Boolean] = for {
        width <- Try(widthField.text.value.toInt)
        height <- Try(heightField.text.value.toInt)
      } yield {
        dialog.close()
        currentSim.stop()
        restartSimulation(width, height)
      }
      a.getOrElse(errorMsg.text_=("Dimensions are invalid"))
    }

    val dialogVbox = new VBox(10,
      new Text("Width: "),
      widthField,
      new Text("Height: "),
      heightField,
      new HBox(10,
        okBtn,
        errorMsg
      )
    )

    val dialogScene = new Scene(dialogVbox, 300, 200)
    dialog.setScene(dialogScene)
    dialog.show()
  }

}
