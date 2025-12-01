package com.example.tpm_e2e_android.robots

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

// 🟦 Текст заголовку екрана логіну
private const val LOGIN_TITLE = "Вітаємо!"

// 🟦 Текст кнопки логіну
private const val LOGIN_BUTTON_TEXT = "Увійти"

// 🟦 Текст заголовку головного екрана
private const val HOME_TITLE = "Головна"

/**
 * 🟦 Robot для роботи з екраном логіну Fixator.
 */
class LoginRobot(
    private val device: UiDevice
) {

    /**
     * 🟦 Перевірка, що екран логіну відкрито (є заголовок "Вітаємо!").
     */
    fun assertLoginScreenVisible(timeout: Long = 10_000L): LoginRobot {
        val appeared = device.wait(
            Until.hasObject(By.text(LOGIN_TITLE)),
            timeout
        )

        assertTrue("Екран логіну не зʼявився за $timeout мс.", appeared)
        return this
    }

    /**
     * 🟦 Пошук усіх полів введення (EditText) на екрані логіну.
     *     Очікуємо, що їх мінімум два: email і пароль.
     */
    private fun findInputFields(timeout: Long = 10_000L): List<UiObject2> {
        // 🟦 Чекаємо, поки зʼявиться хоч один EditText
        device.wait(
            Until.hasObject(By.clazz("android.widget.EditText")),
            timeout
        )

        val fields = device.findObjects(By.clazz("android.widget.EditText"))
        assertTrue(
            "На екрані логіну очікувалося принаймні 2 поля введення, знайдено: ${fields.size}.",
            fields.size >= 2
        )

        return fields
    }

    /**
     * 🟦 Введення email (у перше поле EditText).
     */
    fun typeEmail(value: String): LoginRobot {
        val fields = findInputFields()
        val emailField = fields[0]
        assertNotNull("Поле email (EditText[0]) не знайдено.", emailField)

        emailField.text = value
        return this
    }

    /**
     * 🟦 Введення пароля (у друге поле EditText).
     */
    fun typePassword(value: String): LoginRobot {
        val fields = findInputFields()
        val passwordField = fields[1]
        assertNotNull("Поле пароля (EditText[1]) не знайдено.", passwordField)

        passwordField.text = value
        return this
    }

    /**
     * 🟦 Клік по кнопці "Увійти".
     */
    fun tapLoginButton(): LoginRobot {
        val button = device.findObject(By.text(LOGIN_BUTTON_TEXT))
            ?: device.findObject(By.clazz("android.widget.Button"))

        assertNotNull("Кнопку 'Увійти' не знайдено.", button)
        button.click()

        return this
    }

    /**
     * 🟦 Очікування появи головного екрана після логіну.
     *     Вважаємо, що логін успішний, якщо зʼявився заголовок "Головна".
     */
    fun waitForHomeScreen(timeout: Long = 15_000L): LoginRobot {
        val appeared = device.wait(
            Until.hasObject(By.text(HOME_TITLE)),
            timeout
        )

        assertTrue(
            "Головний екран з заголовком '$HOME_TITLE' не зʼявився протягом $timeout мс.",
            appeared
        )

        return this
    }
}
