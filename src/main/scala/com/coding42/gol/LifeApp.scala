package com.coding42.gol

import javafx.event.{ActionEvent, EventHandler}

import com.coding42.gol.GameOfLife.{LifeBoard, Pos}
import com.coding42.gol.SimulationManager.SimulationConfig

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Slider, TextField}
import scalafx.scene.layout._
import scalafx.scene.text.Text
import scala.util.Try
import scalafx.scene.image.WritableImage
import scalafx.scene.paint.Color
import scalafx.stage.{Modality, Stage}

object LifeApp extends JFXApp {

  val startWidth = 300
  val startHeight = 200
  val canvas = new Canvas(startWidth, startHeight)
  val gc = canvas.graphicsContext2D

  val speedSlider = new Slider()
  speedSlider.setMin(1)
  speedSlider.setMax(100)
  speedSlider.setValue(1)

  val zoomSlider = new Slider()
  zoomSlider.setMin(1)
  zoomSlider.setMax(10)
  zoomSlider.setValue(0)

  val restartBtn = new Button("Restart")
  val okBtn = new Button("OK")

  val widthField = new TextField
  widthField.text = ""+startWidth
  val heightField = new TextField
  heightField.text = ""+startHeight

  val stepText = new Text("0")

  val down = new HBox()
//  down.setStyle("-fx-background-color: #FF0000;")
  down.children_=(List(
    stepText,
    restartBtn,
    speedSlider,
    zoomSlider
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

  private val speedProvider = () => speedSlider.value.value.toInt
  private val stepUpdater = (step: Int) => stepText.text = "Step: " + step
  private val imageUpdater = (image: LifeBoard) => drawBoard(image)

  val generator = SimulationManager.generator(speedProvider, stepUpdater, imageUpdater)
  var currentSim: SimulationManager = _
  restartSimulation()

  def restartSimulation() = {
    val seed = None
    val width = Try(widthField.text.value.toInt).getOrElse(startWidth) // TODO check field has only numbers
    val height = Try(heightField.text.value.toInt).getOrElse(startHeight)
    canvas.width_=(width)
    canvas.height_=(height)
    stage.sizeToScene()
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
          currentSim.stop()
          restartSimulation()
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

  private def drawBoard(lifeBoard: LifeBoard) = {
    val image = new WritableImage(canvas.width.toInt, canvas.height.toInt)
    val pixelWriter = image.pixelWriter

    val center = Pos(canvas.width.toInt/2, canvas.height.toInt/2)
    val mapper = imageMapper(lifeBoard, center, zoomSlider.value.toInt)

    lifeBoard.zipWithIndex.foreach { case (row, x) =>
      row.zipWithIndex.foreach{ case (alive, y) =>

        pixelWriter.setColor(x, y, mapper(x,y))
      }
    }

    gc.drawImage(image, 0, 0)
  }

  private def imageMapper(lifeBoard: LifeBoard, center: Pos, zoom: Int): (Int, Int) => Color = {
    val width = lifeBoard.length
    val height = lifeBoard(0).length
    val widthDiv2: Int = width / 2
    val heightDiv2: Int = height / 2


    (x, y) => {
      val newX = (x - widthDiv2) / zoom + widthDiv2
      val newY = (y - heightDiv2) / zoom + heightDiv2

      if( lifeBoard(newX)(newY) )
        Color.Black
      else
        Color.White
    }
  }

}
