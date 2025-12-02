import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.fail

class HomeRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    private val HOME_EOFFICE_MENU_TEXT = "Е-канцелярія"
    private val LOGOUT_MENU_TEXT = "Вийти"
    private val MENU_BUTTON_TEXT = "Меню"

    /**
     * ✅ Strict wait for home screen.
     * We consider home screen visible when we see either:
     *  - text "Е-канцелярія", or
     *  - a menu button (desc contains "menu"), or
     *  - a button/text "Меню".
     */
    fun assertHomeScreenVisible(timeoutMs: Long = 60_000): HomeRobot {
        val start = System.currentTimeMillis()
        var isHome = false

        while (System.currentTimeMillis() - start < timeoutMs && !isHome) {
            // App UI at all?
            val anyAppUi = device.hasObject(By.pkg("ua.com.fixator.app"))
            if (anyAppUi) {
                val hasEoffice = device.hasObject(By.text(HOME_EOFFICE_MENU_TEXT))
                val hasMenuDesc = device.hasObject(By.descContains("menu"))
                val hasMenuText = device.hasObject(By.textContains(MENU_BUTTON_TEXT))

                if (hasEoffice || hasMenuDesc || hasMenuText) {
                    isHome = true
                    break
                }
            }
            Thread.sleep(500)
        }

        if (!isHome) {
            val textViews = device.findObjects(By.clazz("android.widget.TextView"))
            val debugInfo = textViews
                .take(15)
                .joinToString("\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "Home screen did not become visible within $timeoutMs ms. " +
                        "Expected to see either \"$HOME_EOFFICE_MENU_TEXT\", a 'menu' button, or text '$MENU_BUTTON_TEXT'. " +
                        "Some visible TextViews:\n$debugInfo"
            )
        }

        return this
    }

    fun handlePossibleStartupDialogs(timeoutMs: Long = 60_000): HomeRobot {
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

    fun logoutIfLoggedIn(timeoutMs: Long = 60_000) {
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
