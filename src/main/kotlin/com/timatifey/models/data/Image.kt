package com.timatifey.models.data

import java.awt.image.BufferedImage

data class Image (
        val height: Int,
        val width: Int,
        val image: BufferedImage
): DataPackage.DataObject