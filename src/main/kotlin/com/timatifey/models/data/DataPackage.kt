package com.timatifey.models.data

import java.io.Serializable

data class DataPackage(
    val dataType: DataType,
    val mouse: Mouse? = null,
    val key: Key? = null,
    val scroll: Scroll? = null,
    val imageSize: ImageSize? = null,
    val message: String? = null
): Serializable {
    enum class DataType: Serializable {
        KEY,
        MOUSE,
        MESSAGE,
        IMAGE_SIZE,
        SCROLL
    }
}
