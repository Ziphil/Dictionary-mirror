package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.text.Font
import javafx.util.Builder
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Measurement implements Builder<DoubleClass> {

  private static final Double UNIT_REM = calculateUnitRem()
  private static final Double UNIT_RPX = calculateUnitRpx()
  private static final Double UNIT_FRPX = calculateUnitFrpx()
  private static final Double OVERRIDEN_FONT_SIZE = -1

  private Double $value = 0
  private String $unit

  public DoubleClass build() {
    if ($unit == "rem") {
      return $value * UNIT_REM
    } else if ($unit == "rpx") {
      return $value * UNIT_RPX
    } else if ($unit == "frpx") {
      return $value * UNIT_FRPX
    } else {
      return $value
    }
  }

  public String getRem() {
    return null
  }

  public void setRem(String value) {
    $value = DoubleClass.parseDouble(value)
    $unit = "rem"
  }

  public static Double rem(Int value) {
    return value * UNIT_REM
  }

  public String getRpx() {
    return null
  }

  public void setRpx(String value) {
    $value = DoubleClass.parseDouble(value)
    $unit = "rpx"
  }

  public static Double rpx(Int value) {
    return value * UNIT_RPX
  }

  public String getFrpx() {
    return null
  }

  public void setFrpx(String value) {
    $value = DoubleClass.parseDouble(value)
    $unit = "frpx"
  }

  public static Double frpx(Int value) {
    return value * UNIT_FRPX
  }

  private static Double calculateUnitRem() {
    Double fontSize = (OVERRIDEN_FONT_SIZE >= 0) ? OVERRIDEN_FONT_SIZE : Font.getDefault().getSize()
    return fontSize
  }

  private static Double calculateUnitRpx() {
    Double fontSize = (OVERRIDEN_FONT_SIZE >= 0) ? OVERRIDEN_FONT_SIZE : Font.getDefault().getSize()
    return fontSize / 12
  }

  private static Double calculateUnitFrpx() {
    String system = System.getProperty("os.name").toLowerCase()
    if (system.startsWith("mac")) {
      return calculateUnitRpx() * 1.1
    } else {
      return calculateUnitRpx()
    }
  }
  
}