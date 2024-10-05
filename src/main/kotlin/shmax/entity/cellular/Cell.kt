package shmax.entity.cellular

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import shmax.util.res

class Cell(
    x: Int,
    y: Int
): Cellular(
    x = x,
    y = y,
    type = 2,
    graphic = ImageView(center)
) {

    init {
        children.add(graphic)
    }

    companion object {
        const val WIDTH: Double = 46.0
        const val HEIGHT: Double = 68.0

        val center = res<Image>("sprites/bacteria/Bacteria_Center.png")
        val cellsImageMap = mapOf(
            "0000" to center,
            "2222" to center,
            "0222" to res<Image>("sprites/bacteria/Bacteria_Top.png"),
            "2202" to res<Image>("sprites/bacteria/Bacteria_Bottom.png"),
            "2022" to res<Image>("sprites/bacteria/Bacteria_Right.png"),
            "2220" to res<Image>("sprites/bacteria/Bacteria_Left.png"),
            "0022" to res<Image>("sprites/bacteria/Bacteria_TopRight.png"),
            "0220" to res<Image>("sprites/bacteria/Bacteria_TopLeft.png"),
            "2002" to res<Image>("sprites/bacteria/Bacteria_BottomRight.png"),
            "2200" to res<Image>("sprites/bacteria/Bacteria_BottomLeft.png"),
            "0020" to res<Image>("sprites/bacteria/Bacteria_TopTop.png"),
            "2000" to res<Image>("sprites/bacteria/Bacteria_BottomBottom.png"),
            "0002" to res<Image>("sprites/bacteria/Bacteria_RightRight.png"),
            "0200" to res<Image>("sprites/bacteria/Bacteria_LeftLeft.png"),
            "0202" to res<Image>("sprites/bacteria/Bacteria_TopBottom.png"),
            "2020" to res<Image>("sprites/bacteria/Bacteria_RightLeft.png"),
        )
    }
}