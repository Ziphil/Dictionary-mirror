package ziphil.custom

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PopupPieChartSkin extends SkinBase<PopupPieChart> {

  private PopupPieChart $control
  private StackPane $pane = StackPane.new()
  private Label $captionLabel = Label.new()

  public PopupPieChartSkin(PopupPieChart control) {
    super(control)
    $control = control
    setupPane()
    setupCaptionLabel()
    setupChart()
  }

  private void setupPane() {
    $pane.getChildren().addAll($control.getChart(), $captionLabel)
    getChildren().add($pane)
  }

  private void setupCaptionLabel() {
    $captionLabel.setMouseTransparent(true)
    $captionLabel.setVisible(false)
    $captionLabel.getStyleClass().add("pie-caption")
  }

  private void setupChart() {
    ObjectProperty<ObservableList<PieChart.Data>> data = $control.getChart().dataProperty()
    for (PieChart.Data singleData : data.get()) {
      setupSingleData(singleData)
    }
    data.addListener() { ObservableValue<? extends ObservableList<PieChart.Data>> observableValue, ObservableList<PieChart.Data> oldValue, ObservableList<PieChart.Data> newValue ->
      for (PieChart.Data singleData : newValue) {
        setupSingleData(singleData)
      }
      setupData(newValue)
    }
  }

  private void setupData(ObservableList<PieChart.Data> data) {
    data.addListener() { Change<? extends PieChart.Data> change ->
      while (change.next()) {
        if (change.wasAdded()) {
          for (PieChart.Data singleData : change.getAddedSubList()) {
            setupSingleData(singleData)
          }
        }
      }
    }
  }

  private void setupSingleData(PieChart.Data singleData) {
    Node node = singleData.getNode()
    PieChart chart = $control.getChart()
    node.addEventHandler(MouseEvent.MOUSE_ENTERED) { MouseEvent event ->
      Double totalPieValue = 0
      for (PieChart.Data temporarySingleData : chart.getData()) {
        totalPieValue += temporarySingleData.getPieValue()
      }
      String pieValueString = String.format("%.${$control.getPieValuePrecision()}f", singleData.getPieValue())
      String percentageString = String.format("%.${$control.getPercentagePrecision()}f", singleData.getPieValue() * 100 / totalPieValue)
      $captionLabel.setText("${pieValueString}\n(${percentageString}%)")
      Point2D localPoint = $pane.sceneToLocal(event.getSceneX(), event.getSceneY())
      Double translateX = localPoint.getX() - $pane.getWidth() / 2 + $captionLabel.getWidth() / 2 + 10
      Double translateY = localPoint.getY() - $pane.getHeight() / 2 + $captionLabel.getHeight() / 2 + 15
      $captionLabel.setTranslateX((Int)translateX)
      $captionLabel.setTranslateY((Int)translateY)
      $captionLabel.setVisible(true)
      List<String> styleClass = $captionLabel.getStyleClass()
      if (styleClass.size() > 2) {
        for (Int i = 2 ; i < styleClass.size() ; i ++) {
          styleClass.removeAt(2)
        }
      }
      styleClass.addAll(node.getStyleClass()[1 ..< node.getStyleClass().size()])
    }
    node.addEventHandler(MouseEvent.MOUSE_MOVED) { MouseEvent event ->
      Point2D localPoint = $pane.sceneToLocal(event.getSceneX(), event.getSceneY())
      Double translateX = localPoint.getX() - $pane.getWidth() / 2 + $captionLabel.getWidth() / 2 + 10
      Double translateY = localPoint.getY() - $pane.getHeight() / 2 + $captionLabel.getHeight() / 2 + 15
      $captionLabel.setTranslateX((Int)translateX)
      $captionLabel.setTranslateY((Int)translateY)
    }
    node.addEventHandler(MouseEvent.MOUSE_EXITED) { MouseEvent event ->
      $captionLabel.setVisible(false)
    }
  }

}