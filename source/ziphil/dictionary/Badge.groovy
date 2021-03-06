package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.image.Image
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum Badge {

  CIRCLE("マーカー1", "circle-badge", "circle"),
  SQUARE("マーカー2", "square-badge", "square"),
  UP_TRIANGLE("マーカー3", "up-triangle-badge", "up_triangle"),
  DIAMOND("マーカー4", "diamond-badge", "diamond"),
  DOWN_TRIANGLE("マーカー5", "down-triangle-badge", "down_triangle"),
  CROSS("マーカー6", "cross-badge", "cross"),
  PENTAGON("マーカー7", "pentagon-badge", "pentagon"),
  HEART("マーカー8", "heart-badge", "heart")

  private static final String IMAGE_DIRECTORY = "resource/image/badge/"

  private String $name
  private String $styleClass
  private Image $image

  private Badge(String name, String styleClass, String path) {
    $name = name
    $styleClass = styleClass
    $image = Image.new(getClass().getClassLoader().getResourceAsStream(IMAGE_DIRECTORY + path + ".png"))
  }

  public String toString() {
    return $name
  }

  public String getName() {
    return $name
  }

  public String getStyleClass() {
    return $styleClass
  }

  public Image getImage() {
    return $image
  }

}