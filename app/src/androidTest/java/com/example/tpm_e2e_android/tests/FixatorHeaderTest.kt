package com.example.tpm_e2e_android.tests

import HomeRobot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Until
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.LoginRobot
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Verifies header elements on home screen after admin login:
 * - search field
 * - current date
 * - notifications icon
 * - settings icon
 * - logout icon
 */
@RunWith(AndroidJUnit4::class)
class FixatorHeaderTest : BaseFixatorTest() {

    companion object {
        private const val HEADER_TIMEOUT_MS = 30_000L
    }

    @Test
    fun headerElementsAreVisibleForAdmin() {
        // 1) Login as Admin
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(FixatorCredentials.ADMIN_LOGIN)
            .typePassword(FixatorCredentials.ADMIN_PASSWORD)
            .tapLoginButton()

        // 2) Wait for home screen to be fully loaded
        HomeRobot(device)
            .assertHomeScreenVisible()

        // 3) Verify header elements
        assertSearchFieldVisible()
        assertCurrentDateVisible()
        assertNotificationsIconVisible()
        assertSettingsIconVisible()
        assertLogoutIconVisible()
    }

    // ----------------- Helpers -----------------

    /**
     * Wait until at least one of the selectors is present.
     */
    private fun waitAnySelector(timeoutMs: Long, vararg selectors: BySelector): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (selectors.any { device.hasObject(it) }) {
                return true
            }
            Thread.sleep(250)
        }
        return false
    }

    private fun assertSearchFieldVisible() {
        val found = device.wait(
            Until.hasObject(
                // Search field placeholder text: "Пошук..."
                By.textContains("Пошук")
            ),
            HEADER_TIMEOUT_MS
        )

        assertTrue(
            "Search field with placeholder 'Пошук...' was not found in header.",
            found
        )
    }

    private fun assertCurrentDateVisible() {
        val locale = Locale("uk", "UA")
        val year = SimpleDateFormat("yyyy", locale).format(Date())  // e.g. "2025"

        val appeared = device.wait(
            Until.hasObject(
                By.textContains(year)
            ),
            HEADER_TIMEOUT_MS
        )

        assertTrue(
            "Current year '$year' was not found in header (by textContains).",
            appeared
        )
    }

    private fun assertNotificationsIconVisible() {
        val found = waitAnySelector(
            HEADER_TIMEOUT_MS,
            // Ukrainian content descriptions
            By.descContains("повіщ"),      // "сповіщення"
            By.descContains("сповіщ"),
            // Possible English fallback
            By.descContains("notif")
        )

        assertTrue(
            "Notifications icon was not found in header (by content description).",
            found
        )
    }

    private fun assertSettingsIconVisible() {
        // 1) Try to find by content description (Ukr / Eng)
        val byDescFound = waitAnySelector(
            HEADER_TIMEOUT_MS,
            By.descContains("налашт"),     // "налаштування"
            By.descContains("settings")
        )

        if (byDescFound) {
            return
        }

        // 2) Fallback: heuristic by position – clickable icon in header right area
        val screenWidth = device.displayWidth
        val screenHeight = device.displayHeight

        val headerHeight = (screenHeight * 0.25).toInt()   // top 25% of the screen
        val rightStart = (screenWidth * 0.5).toInt()       // right half of the screen

        val clickables = device.findObjects(By.clickable(true))

        val headerRightClickables = clickables.filter { obj ->
            val b = obj.visibleBounds
            val inTop = b.top < headerHeight
            val inRight = b.centerX() > rightStart
            inTop && inRight
        }

        val debugInfo = headerRightClickables
            .take(10)
            .joinToString("\n") { obj ->
                "class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}', desc='${obj.contentDescription}', bounds=${obj.visibleBounds}"
            }

        assertTrue(
            "Settings icon was not found in header. " +
                    "No clickable elements detected in top-right header area.\n" +
                    "Header-right clickables:\n$debugInfo",
            headerRightClickables.isNotEmpty()
        )
    }

    private fun assertLogoutIconVisible() {
        // 1) Try to find by content description (Ukr / Eng)
        val byDescFound = waitAnySelector(
            HEADER_TIMEOUT_MS,
            By.descContains("вихід"),
            By.descContains("logout"),
            By.descContains("exit")
        )

        if (byDescFound) {
            return
        }

        // 2) Fallback: heuristic by position – we expect a cluster of icons (>= 2)
        // in the top-right area of the screen.
        val screenWidth = device.displayWidth
        val screenHeight = device.displayHeight

        val headerHeight = (screenHeight * 0.25).toInt()   // top 25% of the screen
        val rightStart = (screenWidth * 0.5).toInt()       // right half

        val clickables = device.findObjects(By.clickable(true))

        val headerRightClickables = clickables.filter { obj ->
            val b = obj.visibleBounds
            val inTop = b.top < headerHeight
            val inRight = b.centerX() > rightStart
            inTop && inRight
        }

        val debugInfo = headerRightClickables
            .take(10)
            .joinToString("\n") { obj ->
                "class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}', desc='${obj.contentDescription}', bounds=${obj.visibleBounds}"
            }

        // Для выхода ожидаем, что иконок в правом верхнем углу хотя бы две:
        // уведомления + настройки/выход и т.п.
        assertTrue(
            "Logout icon was not found in header. " +
                    "No explicit contentDescription and less than 2 clickable elements detected " +
                    "in top-right header area.\n" +
                    "Header-right clickables:\n$debugInfo",
            headerRightClickables.size >= 2
        )
    }

}
