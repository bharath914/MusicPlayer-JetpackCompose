package com.bharath.musicplayerforbaby.other

open class Event<out T>(private val data: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            data
        }
    }

    fun peekContent() = data
}