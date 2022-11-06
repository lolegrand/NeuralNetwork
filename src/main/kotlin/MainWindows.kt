import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.awt.image.BufferedImage
import kotlin.concurrent.thread

fun main() {
    launch<Main>()
}

class Main : App(ImageView::class) {
    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 500.0
            minHeight = 500.0
            super.start(stage)
        }
    }
}

class NetworkController : Controller() {
    val imageProperty = SimpleObjectProperty<Image>()
    private var img = BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB)
    init {
        thread(isDaemon = true) {
            for (x in 0 until   500){
                for (y in 0 until  500) {
                    img.setRGB(x, y, 0xFF0000)
                }
            }
            imageProperty.set(SwingFXUtils.toFXImage(img, null))
        }
    }
}

class ImageView : View("Networking") {
    private val controller : NetworkController by inject()

    override val root: Parent = borderpane {
        center {
            hbox {
                stackpane {
                    imageview {
                        imageProperty().bind(controller.imageProperty)
                    }
                }
            }
        }
    }
}
