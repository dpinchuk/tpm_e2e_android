package com.example.tpm_e2e_android.robots

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.assertTrue

class EOfficeRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    /**
     * Check that "Incoming documents" screen is visible.
     * Uses text "Вхідн" and a long timeout because emulator is slow.
     */
    fun assertIncomingDocumentsScreenVisible(timeoutMs: Long = 60_000): EOfficeRobot {
        val appeared = device.wait(
            Until.hasObject(
                By.textContains("Вхідн") // "Вхідні", "Вхідні документи", etc.
            ),
            timeoutMs
        )

        assertTrue(
            "Incoming documents screen did not appear within $timeoutMs ms. " +
                    "Maybe the title text has changed or screen loaded too slowly.",
            appeared
        )

        return this
    }

    /**
     * Open the first incoming document in the list.
     */
    fun openFirstIncomingDocument(): EOfficeRobot {
        val scrollable = UiScrollable(UiSelector().scrollable(true))

        try {
            scrollable.scrollToBeginning(10)
        } catch (_: Exception) {
            // ignore if not scrollable
        }

        val firstItem = try {
            scrollable.getChildByInstance(
                UiSelector().clickable(true),
                0
            )
        } catch (e: Exception) {
            null
        }

        if (firstItem != null) {
            firstItem.click()
            return this
        }

        val candidates = device.findObjects(By.clickable(true))
        val candidate = candidates.firstOrNull {
            val text = it.text
            !text.isNullOrBlank()
        } ?: throw IllegalStateException(
            "Could not find any clickable item to open in Incoming documents list. " +
                    "Maybe the current user has no incoming documents or UI structure changed."
        )

        candidate.click()
        return this
    }

    fun assertOutgoingDocumentsScreenVisible(timeoutMs: Long = 30_000): EOfficeRobot {
        val appeared = device.wait(
            Until.hasObject(
                // Page title "Вихідні документи"
                By.textContains("Вихідні документи")
            ),
            timeoutMs
        )

        assertTrue(
            "Outgoing documents screen did not appear within $timeoutMs ms. " +
                    "Maybe the title text has changed or screen loaded too slowly.",
            appeared
        )

        return this
    }

    /**
     * Checks that outgoing documents table has at least one row.
     */
    fun assertOutgoingDocumentsListNotEmpty(): EOfficeRobot {
        val scrollable = UiScrollable(UiSelector().scrollable(true))

        // Try to scroll to top just in case
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
            "Outgoing documents list appears to be empty or table structure is different.",
            firstRow != null
        )

        return this
    }

}
