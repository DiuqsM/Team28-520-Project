package com.example.happnin.data

import org.junit.Assert.assertEquals
import org.junit.Test

class EventStatusTest {

    @Test
    fun openStatus_labelIsOpen() {
        assertEquals("Open", EventStatus.OPEN.label)
    }

    @Test
    fun fullStatus_labelIsFull() {
        assertEquals("Full", EventStatus.FULL.label)
    }

    @Test
    fun cancelledStatus_labelIsCancelled() {
        assertEquals("Cancelled", EventStatus.CANCELLED.label)
    }
}
