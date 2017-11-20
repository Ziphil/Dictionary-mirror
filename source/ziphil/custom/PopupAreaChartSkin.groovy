package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PopupAreaChartSkin<X, Y> extends SkinBase<PopupAreaChart<X, Y>> {

  private PopupAreaChart<X, Y> $control
  private StackPane $pane = StackPane.new()
  private VBox $captionBox = VBox.new()
  private Label $captionLabel = Label.new()

  public PopupAreaChartSkin(PopupAreaChart control) {
    super(control)
    $control = control
    setupPane()
    setupCaptionBox()
    setupCaptionLabel()
    setupChart()
  }

  private void setupPane() {
    Region arrow = Region.new()
    arrow.setPrefSize(5, 6)
    arrow.getStyleClass().addAll("caption-arrow", "default-color0")
    $captionBox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE)
    $captionBox.setFillWidth(false)
    $captionBox.setAlignment(Pos.CENTER)
    $captionBox.getChildren().addAll(arrow, $captionLabel)
    $pane.getChildren().addAll($control.getChart(), $captionBox)
    getChildren().add($pane)
  }

  private void setupCaptionBox() {
    $captionBox.setMouseTransparent(true)
    $captionBox.setVisible(false)
  }

  private void setupCaptionLabel() {
    $captionLabel.getStyleClass().addAll("chart-caption", "default-color0")
  }

  private void setupChart() {
    Node background = $control.getChart().lookup(".chart-plot-background")
    background.addEventHandler(MouseEvent.MOUSE_MOVED) { MouseEvent event ->
      Axis<X> xAxis = $control.getChart().getXAxis()
      Axis<Y> yAxis = $control.getChart().getYAxis()
      XYChart.Data<X, Y> singleData = correspondingSingleData(nearestXValue(event.getX()))
      if (yAxis instanceof ValueAxis) {
        String text = yAxis.getTickLabelFormatter().toString(singleData.getYValue())
        $captionLabel.setText(text)
      } else {
        String text = singleData.getYValue().toString()
        $captionLabel.setText(text)
      }
      Double translateX = xAxis.getDisplayPosition(singleData.getXValue()) - $pane.getWidth() / 2 + yAxis.getWidth()
      Double translateY = yAxis.getDisplayPosition(singleData.getYValue()) - $pane.getHeight() / 2 + $captionBox.getHeight() / 2 + 2 
      $captionBox.setTranslateX((Int)translateX)
      $captionBox.setTranslateY((Int)translateY)
      $captionBox.setVisible(true)
    }
    background.addEventHandler(MouseEvent.MOUSE_EXITED) { MouseEvent event ->
      $captionBox.setVisible(false)
    }
    for (Node node : background.getParent().getChildrenUnmodifiable()) {
      if (node != background) {
        node.setMouseTransparent(true)
      }
    }
    $control.getChart().setCreateSymbols(false)
  }

  private X nearestXValue(Double mouseX) {
    Axis<X> xAxis = $control.getChart().getXAxis()
    X xValue = xAxis.getValueForDisplay(mouseX)
    if (xValue instanceof Number) {
      X nearestXValue = null
      Double minDistance = DoubleClass.POSITIVE_INFINITY
      for (XYChart.Data<X, Y> singleData : $control.getChart().getData()[0].getData()) {
        Number singleXValue = (Number)singleData.getXValue()
        Double distance = Math.abs(singleXValue.doubleValue() - xValue.doubleValue())
        if (distance < minDistance) {
          nearestXValue = singleData.getXValue()
          minDistance = distance
        }
      }
      return nearestXValue
    } else {
      return xValue
    }
  }

  private XYChart.Data<X, Y> correspondingSingleData(X xValue) {
    for (XYChart.Data<X, Y> singleData : $control.getChart().getData()[0].getData()) {
      if (singleData.getXValue() == xValue) {
        return singleData
      }
    }
    return null
  }

}