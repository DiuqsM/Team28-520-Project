package com.example.happnin.ui.events

import com.example.happnin.data.Event
import com.example.happnin.data.EventStatus
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class EventFormattersTest {

    private fun makeEvent(startsAt: LocalDateTime, endsAt: LocalDateTime) = Event(
        id = "test", createdBy = null, source = "test", title = "Test Event",
        description = "", location = "Test Location", college = "Test College",
        price = 0.0, ageLimit = null, capacity = null, registrationCount = 0,
        status = EventStatus.OPEN, startsAt = startsAt, endsAt = endsAt,
        createdAt = LocalDateTime(2026, 1, 1, 0, 0),
    )

    // toDateLabel tests

    @Test
    fun toDateLabel_formatsMonthAndDayCorrectly() {
        val dt = LocalDateTime(2026, 4, 30, 0, 0)
        assertEquals("April 30", dt.toDateLabel())
    }

    @Test
    fun toDateLabel_handlesSingleDigitDay() {
        val dt = LocalDateTime(2026, 5, 3, 0, 0)
        assertEquals("May 3", dt.toDateLabel())
    }

    @Test
    fun toDateLabel_handlesJanuary() {
        val dt = LocalDateTime(2026, 1, 15, 0, 0)
        assertEquals("January 15", dt.toDateLabel())
    }

    @Test
    fun toDateLabel_handlesDecember() {
        val dt = LocalDateTime(2026, 12, 31, 0, 0)
        assertEquals("December 31", dt.toDateLabel())
    }

    // toTimeLabel tests

    @Test
    fun toTimeLabel_morningHour_formatsAsAm() {
        val dt = LocalDateTime(2026, 1, 1, 9, 30)
        assertEquals("9:30am", dt.toTimeLabel())
    }

    @Test
    fun toTimeLabel_afternoonHour_formatsAsPm() {
        val dt = LocalDateTime(2026, 1, 1, 14, 0)
        assertEquals("2:00pm", dt.toTimeLabel())
    }

    @Test
    fun toTimeLabel_midnight_formats12am() {
        val dt = LocalDateTime(2026, 1, 1, 0, 0)
        assertEquals("12:00am", dt.toTimeLabel())
    }

    @Test
    fun toTimeLabel_noon_formats12pm() {
        val dt = LocalDateTime(2026, 1, 1, 12, 0)
        assertEquals("12:00pm", dt.toTimeLabel())
    }

    @Test
    fun toTimeLabel_paddingMinutes() {
        val dt = LocalDateTime(2026, 1, 1, 10, 5)
        assertEquals("10:05am", dt.toTimeLabel())
    }

    // toDateRangeLabel tests

    @Test
    fun toDateRangeLabel_differentTimes_showsTimeRange() {
        val event = makeEvent(
            startsAt = LocalDateTime(2026, 4, 30, 19, 0),
            endsAt = LocalDateTime(2026, 4, 30, 21, 0),
        )
        assertEquals("April 30, 7:00pm–9:00pm", event.toDateRangeLabel())
    }

    @Test
    fun toDateRangeLabel_sameStartAndEnd_showsSingleTime() {
        val dt = LocalDateTime(2026, 4, 30, 19, 0)
        val event = makeEvent(startsAt = dt, endsAt = dt)
        assertEquals("April 30, 7:00pm", event.toDateRangeLabel())
    }

    @Test
    fun toDateRangeLabel_morningEvent_usesAmLabels() {
        val event = makeEvent(
            startsAt = LocalDateTime(2026, 5, 2, 9, 30),
            endsAt = LocalDateTime(2026, 5, 2, 11, 0),
        )
        assertEquals("May 2, 9:30am–11:00am", event.toDateRangeLabel())
    }
}
