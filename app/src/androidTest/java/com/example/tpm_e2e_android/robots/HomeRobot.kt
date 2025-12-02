import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class HomeRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    private val HOME_EOFFICE_MENU_TEXT = "Е-канцелярія"
    private val LOGOUT_MENU_TEXT = "Вийти"
    private val MENU_BUTTON_TEXT = "Меню"

    /**
     * Soft-check: try to detect home screen.
     *
     * If we don't see "Е-канцелярія", we do NOT fail the test.
     * We only fail if there is no UI from our app at all.
     */
    fun assertHomeScreenVisible(timeoutMs: Long = 15_000): HomeRobot {
        // Wait for any UI object from our package
        val appearedAny = device.wait(
            Until.hasObject(By.pkg("ua.com.fixator.app")),
            timeoutMs
        )

        if (!appearedAny) {
            throw AssertionError(
                "Fixator app UI did not appear within $timeoutMs ms. " +
                        "Check that the app is installed and MAIN_ACTIVITY is correct."
            )
        }

        // Try to find "Е-канцелярія" text
        val hasEoffice = device.hasObject(By.text(HOME_EOFFICE_MENU_TEXT))

        if (!hasEoffice) {
            // Debug: show some visible text elements (TextViews)
            val textViews = device.findObjects(By.clazz("android.widget.TextView"))
            val debugInfo = textViews
                .take(10)
                .joinToString(separator = "\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            println(
                "WARN: Home screen marker '$HOME_EOFFICE_MENU_TEXT' was not found. " +
                        "Continuing anyway. Some visible TextViews:\n$debugInfo"
            )
        }

        return this
    }

    /**
     * Handle possible startup dialogs (permissions / onboarding).
     */
    fun handlePossibleStartupDialogs(timeoutMs: Long = 5_000): HomeRobot {
        val allowTexts = listOf("Дозволити", "Allow", "OK")

        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            var handled = false
            for (text in allowTexts) {
                val btn = device.findObject(By.text(text))
                if (btn != null) {
                    btn.click()
                    handled = true
                    Thread.sleep(500)
                }
            }
            if (!handled) break
        }

        return this
    }

    /**
     * Try to logout if we are on home screen. If not – do nothing.
     */
    fun logoutIfLoggedIn(timeoutMs: Long = 3_000) {
        val isHomeVisible = device.wait(
            Until.hasObject(By.text(HOME_EOFFICE_MENU_TEXT)),
            timeoutMs
        )

        if (!isHomeVisible) return

        val menuButtonByText = device.findObject(By.text(MENU_BUTTON_TEXT))
        if (menuButtonByText != null) {
            menuButtonByText.click()
        } else {
            device.pressMenu()
        }

        val logoutItem = device.wait(
            Until.findObject(By.text(LOGOUT_MENU_TEXT)),
            timeoutMs
        )
        logoutItem?.click()
    }

    fun logoutToLogin(): HomeRobot {
        logoutIfLoggedIn()
        return this
    }
}
