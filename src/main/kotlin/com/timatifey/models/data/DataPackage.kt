package com.timatifey.models.data

data class DataPackage (
        val dataType: DataType,
        val dataObject: DataObject
) {
    enum class DataType {
        KEY,
        MOUSE,
        IMAGE
    }
    
    class DataObject
}
