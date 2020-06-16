package com.timatifey.models.data

import java.io.Serializable

data class DataPackage(
    val data: Data
): Serializable {
    enum class DataType {
        MOUSE,
        KEY,
        SCROLL,
        MESSAGE,
        IMAGE_SIZE
    }
}

interface Data: Serializable