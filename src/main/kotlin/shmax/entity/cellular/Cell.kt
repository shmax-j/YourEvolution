package shmax.entity.cellular

import javafx.scene.image.ImageView
import shmax.generated.R

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

        val center = R.images.sprite_bacteria_bacteria_center
        val cellsImageMap = mapOf(
            "0000" to center,
            "2222" to center,
            "0222" to R.images.sprite_bacteria_bacteria_top,
            "2202" to R.images.sprite_bacteria_bacteria_bottom,
            "2022" to R.images.sprite_bacteria_bacteria_right,
            "2220" to R.images.sprite_bacteria_bacteria_left,
            "0022" to R.images.sprite_bacteria_bacteria_topright,
            "0220" to R.images.sprite_bacteria_bacteria_topleft,
            "2002" to R.images.sprite_bacteria_bacteria_bottomright,
            "2200" to R.images.sprite_bacteria_bacteria_bottomleft,
            "0020" to R.images.sprite_bacteria_bacteria_toptop,
            "2000" to R.images.sprite_bacteria_bacteria_bottombottom,
            "0002" to R.images.sprite_bacteria_bacteria_rightright,
            "0200" to R.images.sprite_bacteria_bacteria_leftleft,
            "0202" to R.images.sprite_bacteria_bacteria_topbottom,
            "2020" to R.images.sprite_bacteria_bacteria_rightleft,
        )
    }
}