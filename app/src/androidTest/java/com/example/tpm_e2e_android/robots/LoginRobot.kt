package com.example.tpm_e2e_android.robots

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.fail

class LoginRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    // TODO: если узнаешь реальные id полей – подставь сюда
    private val EMAIL_RES_ID = ""      // e.g. "ua.com.fixator.app:id/emailEditText"
    private val PASSWORD_RES_ID = ""   // e.g. "ua.com.fixator.app:id/passwordEditText"

    /**
     * ✅ Check that login screen is visible.
     * If login is not visible but home is already shown – we do NOT fail.
     */
    fun assertLoginScreenVisible(timeoutMs: Long = 15_000): LoginRobot {
        // Possible markers of login screen
        val loginMarkers = listOf(
            By.textContains("Логін"),
            By.textContains("Увійти"),
            By.textContains("Вхід"),
            By.textContains("Войти"),
        )

        var loginAppeared = false
        val start = System.currentTimeMillis()

        while (System.currentTimeMillis() - start < timeoutMs && !loginAppeared) {
            for (marker in loginMarkers) {
                if (device.hasObject(marker)) {
                    loginAppeared = true
                    break
                }
            }
            if (!loginAppeared) {
                Thread.sleep(500)
            }
        }

        if (!loginAppeared) {
            // Maybe we are already on home screen
            val isHomeVisible = device.hasObject(By.text("Е-канцелярія"))
            if (!isHomeVisible) {
                fail(
                    "Login screen did not appear within $timeoutMs ms, " +
                            "and home screen is not visible either. App is in unknown state."
                )
            }
            // If home is visible – do not fail, just continue.
        }

        return this
    }

    /**
     * ✅ Type email into email field.
     * Tries resource-id first, then falls back to the first EditText.
     */
    fun typeEmail(email: String): LoginRobot {
        var emailField: UiObject2? = null

        // 1) By resource-id, if provided
        if (EMAIL_RES_ID.isNotBlank()) {
            emailField = device.findObject(By.res(EMAIL_RES_ID))
        }

        // 2) Fallback: first EditText
        if (emailField == null) {
            val inputs = device.findObjects(By.clazz("android.widget.EditText"))
            if (inputs.isEmpty()) {
                fail("No EditText fields found on login screen when typing email.")
            }
            emailField = inputs.first()
        }

        emailField!!.text = ""
        emailField.text = email

        return this
    }

    /**
     * ✅ Type password into password field.
     * Tries resource-id, then heuristics, then falls back to the only EditText (with warning).
     */
    fun typePassword(password: String): LoginRobot {
        var passwordField: UiObject2? = null

        val inputs = device.findObjects(By.clazz("android.widget.EditText"))

        if (inputs.isEmpty()) {
            fail("No EditText fields found on login screen when typing password.")
        }

        // 1) By resource-id, if provided
        if (PASSWORD_RES_ID.isNotBlank()) {
            passwordField = device.findObject(By.res(PASSWORD_RES_ID))
        }

        // 2) Try to find field with 'pass' in resourceName
        if (passwordField == null) {
            passwordField = inputs.firstOrNull { input ->
                val resName = input.resourceName ?: ""
                resName.contains("pass", ignoreCase = true)
            }
        }

        // 3) If we have at least two EditTexts – use the second as password field
        if (passwordField == null && inputs.size >= 2) {
            passwordField = inputs[1]
        }

        // 4) If only one EditText – fallback to that one (log a warning, but do NOT fail)
        if (passwordField == null && inputs.size == 1) {
            println(
                "WARNING: Only one EditText found on login screen. " +
                        "Using the same field for password. " +
                        "Check login screen structure and adjust selectors in LoginRobot."
            )
            passwordField = inputs[0]
        }

        if (passwordField == null) {
            // Extra debug info
            val debugInfo = inputs.joinToString(separator = "\n") { obj ->
                "class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}'"
            }
            fail(
                "Could not locate password field on login screen. " +
                        "Collected EditText fields:\n$debugInfo"
            )
        }

        passwordField!!.text = ""
        passwordField.text = password

        return this
    }

    /**
     * ✅ Tap on login button.
     * Tries resource-id first, then falls back to text search.
     */
    fun tapLoginButton(): LoginRobot {
        // TODO: if you know the real resource-id of login button – set it here.
        val LOGIN_BUTTON_RES_ID = "" // e.g. "ua.com.fixator.app:id/btnLogin"

        var button: UiObject2? = null

        // 1) Try by resource-id (most stable way)
        if (LOGIN_BUTTON_RES_ID.isNotBlank()) {
            button = device.findObject(By.res(LOGIN_BUTTON_RES_ID))
        }

        // 2) Fallback: try by button text
        if (button == null) {
            val loginButtonTexts = listOf(
                "Увійти",
                "Вхід",
                "Увійти в систему",
                "Войти",
                "Login"
            )

            for (text in loginButtonTexts) {
                val candidate = device.findObject(By.textContains(text))
                if (candidate != null) {
                    button = candidate
                    break
                }
            }
        }

        if (button == null) {
            // Debug output: show some clickable items on screen
            val clickables = device.findObjects(By.clickable(true))
            val debugInfo = clickables
                .take(5)
                .joinToString(separator = "\n") { obj ->
                    "class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "Login button was not found on the login screen. " +
                        "Tried resource-id '$LOGIN_BUTTON_RES_ID' and common login texts. " +
                        "Some clickable elements on screen:\n$debugInfo"
            )
        }

        button!!.click()
        return this
    }
}
