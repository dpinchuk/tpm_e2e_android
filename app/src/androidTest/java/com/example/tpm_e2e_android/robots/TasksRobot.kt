package com.example.tpm_e2e_android.robots

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.assertTrue

class TasksRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    /**
     * Checks that the Tasks screen is visible (title "Задачі").
     */
    fun assertTasksScreenVisible(timeoutMs: Long = 30_000): TasksRobot {
        val appeared = device.wait(
            Until.hasObject(
                By.textContains("Задачі")
            ),
            timeoutMs
        )

        assertTrue(
            "Tasks screen (title 'Задачі') did not appear within $timeoutMs ms.",
            appeared
        )

        return this
    }

    /**
     * Checks that the tasks table has at least one row.
     */
    fun assertTasksListNotEmpty(): TasksRobot {
        val scrollable = UiScrollable(UiSelector().scrollable(true))

        try {
            scrollable.scrollToBeginning(10)
        } catch (_: Exception) {
            // ignore if not scrollable
        }

        val firstRow = try {
            scrollable.getChildByInstance(
                UiSelector().clickable(true),
                0
            )
        } catch (e: Exception) {
            null
        }

        assertTrue(
            "Tasks list appears to be empty or table structure is different.",
            firstRow != null
        )

        return this
    }
}
