package com.timatifey.models.data

data class DataPackage(
    val dataType: DataType,
    val mouse: Mouse? = null,
    val key: Key? = null,
    val image: Image? = null
) {
    enum class DataType {
        KEY,
        MOUSE,
        IMAGE
    }
}
