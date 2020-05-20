package com.timatifey.models.data

import javafx.scene.input.KeyCode

data class Key(val eventType: KeyEventType,
               val character: String,
               val text: String,
               val code: KeyCode,
               val shiftDown: Boolean,
               val controlDown: Boolean,
               val altDown: Boolean,
               val metaDown: Boolean
) {
    enum class KeyEventType {
        ANY,
        KEY_PRESSED,
        KEY_RELEASED,
        KEY_TYPED
    }
}