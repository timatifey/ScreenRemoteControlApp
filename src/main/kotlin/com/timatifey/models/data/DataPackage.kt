package com.timatifey.models.data

data class DataPackage (
        val dataType: DataType,
        val dataObject: Any
) {
    enum class DataType {
        KEY,
        MOUSE,
        IMAGE
    }
    
    interface DataObject
}
