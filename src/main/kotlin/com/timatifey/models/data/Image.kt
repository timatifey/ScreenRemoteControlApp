package com.timatifey.models.data

data class Image (
        val height: Int,
        val width: Int,
        val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (height != other.height) return false
        if (width != other.width) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}