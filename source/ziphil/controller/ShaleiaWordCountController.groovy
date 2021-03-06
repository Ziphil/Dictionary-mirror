package ziphil.controller

import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import ziphil.custom.Measurement
import ziphil.custom.PopupAreaChart
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaWordCountController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/sheleia_word_count.fxml"
  private static final String TITLE = "単語数グラフ"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private VBox $mainPane
  @FXML private PopupAreaChart<Number, Number> $wordCountChart
  @FXML private Spinner<Integer> $startDateControl
  @FXML private Spinner<Integer> $endDateControl
  private List<XYChart.Data<Number, Number>> $data

  public ShaleiaWordCountController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, true)
  }

  @FXML
  private void initialize() {
    setupChart()
  }

  public void prepare(List<XYChart.Data<Number, Number>> data) {
    $data = data
    prepareDateControls()
  }

  private void prepareDateControls() {
    IntegerSpinnerValueFactory startDateValueFactory = (IntegerSpinnerValueFactory)$startDateControl.getValueFactory()
    IntegerSpinnerValueFactory endDateValueFactory = (IntegerSpinnerValueFactory)$endDateControl.getValueFactory()
    Int maxDate = (Int)$data.last().getXValue()
    startDateValueFactory.setMax(maxDate)
    startDateValueFactory.setMin(1000)
    endDateValueFactory.setMax(maxDate)
    endDateValueFactory.setMin(1)
    $startDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue > $endDateControl.getValue()) {
        $endDateControl.getValueFactory().setValue(newValue)
      }
      resetData()
    }
    $endDateControl.valueProperty().addListener() { ObservableValue<? extends IntegerClass> observableValue, IntegerClass oldValue, IntegerClass newValue ->
      if (newValue < $startDateControl.getValue()) {
        $startDateControl.getValueFactory().setValue(newValue)
      }
      resetData()
    }
    startDateValueFactory.setValue(1000)
    endDateValueFactory.setValue(maxDate)
  }

  private void resetData() {
    Int startDate = $startDateControl.getValue()
    Int endDate = $endDateControl.getValue()
    ObservableList<XYChart.Data<Number, Number>> nextData = FXCollections.observableArrayList()
    for (XYChart.Data<Number, Number> singleData : $data) {
      if (singleData.getXValue() >= startDate && singleData.getXValue() <= endDate) {
        nextData.add(singleData)
      }
    }
    XYChart.Series<Number, Number> series = XYChart.Series.new(nextData)
    series.setName("単語数")
    $wordCountChart.getChart().getData().clear()
    $wordCountChart.getChart().getData().add(series)
    NumberAxis xAxis = (NumberAxis)$wordCountChart.getChart().getXAxis()
    xAxis.setLowerBound(startDate)
    xAxis.setUpperBound(endDate)
    Platform.runLater() {
      adjustTickUnit(xAxis, 35)
    }
  }

  // axis の大目盛り単位を、最小値と最大値から自動的に計算して設定し直します。
  // このとき、大目盛りの個数が 20 個より多くはならないように、かつ大目盛りの間に少なくとも minGap の間隔が空くようにします。
  // 大目盛り単位は、最小でも 1 以上の整数で、1, 2.5, 5 のいずれかに 10 の冪をかけたもの (2.5 だった場合は 2) になります。
  // このメソッドは axis のサイズを取得して利用するので、axis がシーングラフに含まれていることを実行前に確認してください。
  private void adjustTickUnit(NumberAxis axis, Double minGap) {
    Double lowerBound = axis.getLowerBound()
    Double upperBound = axis.getUpperBound()
    Double range = upperBound - lowerBound
    Double axisLength = (axis.getSide().isHorizontal()) ? axis.getWidth() : axis.getHeight()
    Int tickMarkCount = (Int)Math.max(Math.floor(axisLength / minGap), 2)
    Double tickUnit = range / tickMarkCount
    Int count = 30
    while (count > 20) {
      Int exponent = (Int)Math.floor(Math.log10(tickUnit))
      Double mantissa = tickUnit / Math.pow(10, exponent)
      Double ratio = mantissa
      if (mantissa > 5) {
        exponent ++
        ratio = 1
      } else if (mantissa > 2.5) {
        ratio = 5
      } else {
        ratio = 2.5
      }
      tickUnit = ratio * Math.pow(10, exponent)
      count = (Int)Math.ceil((upperBound - lowerBound) / tickUnit)
      if (tickMarkCount == 2) {
        break
      }
      if (count > 20) {
        tickUnit *= 2
      }
    }
    tickUnit = Math.floor(Math.max(tickUnit, 1))
    axis.setTickUnit(tickUnit)
  }

  private void setupChart() {
    NumberAxis xAxis = (NumberAxis)$wordCountChart.getChart().getXAxis()
    NumberAxis yAxis = (NumberAxis)$wordCountChart.getChart().getYAxis()
    xAxis.setTickLabelFormatter(NumberStringConverter.new("0"))
    yAxis.setTickLabelFormatter(NumberStringConverter.new("0"))
  }

}