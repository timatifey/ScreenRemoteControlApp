package com.timatifey.models.data

interface ClientListElement {
    val ip: String
    val dataSharingTypes: MutableList<DataPackage.DataType>
    var needStop: Boolean
}