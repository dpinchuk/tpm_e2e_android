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
     * ✅ Check that "Incoming documents" screen is visible.
     * Uses text instead of resource-id to avoid fragile selectors.
     */
    fun assertIncomingDocumentsScreenVisible(timeoutMs: Long = 10_000): EOfficeRobot {
        val appeared = device.wait(
            Until.hasObject(
                By.textContains("Вхідн") // "Вхідні", "Вхідні документи", etc.
            ),
            timeoutMs
        )

        assertTrue(
            "Incoming documents screen did not appear within $timeoutMs ms. " +
                    "Maybe the title text has changed or a different screen is shown.",
            appeared
        )

        return this
    }

    /**
     * ✅ Open the first incoming document in the list.
     */
    fun openFirstIncomingDocument(): EOfficeRobot {
        // Try to use first scrollable list on the screen
        val scrollable = UiScrollable(UiSelector().scrollable(true))

        try {
            scrollable.scrollToBeginning(10)
        } catch (_: Exception) {
            // ignore if list is not scrollable
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

        // Fallback: any clickable element with non-empty text
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
}
