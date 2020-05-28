package com.timatifey.models.data

import java.net.Socket

interface ClientListElement {
    var sockets: MutableList<Socket>
    var needStop: Boolean
}