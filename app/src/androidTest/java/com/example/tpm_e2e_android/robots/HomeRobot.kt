package com.example.tpm_e2e_android.robots

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue

// 🟦 Тексти, які ідентифікують екрани
private const val HOME_TITLE = "Головна"
private const val LOGIN_TITLE = "Вітаємо!"

/**
 * 🟦 Robot для головного екрана Fixator.
 */
class HomeRobot(
    private val device: UiDevice
) {

    /**
     * 🟦 Перевірка, що головний екран відкритий (бачимо текст "Головна").
     */
    fun assertHomeScreenVisible(timeout: Long = 10_000L): HomeRobot {
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

    /**
     * 🟦 Виконати вихід із акаунта:
     *   1) Переконатися, що ми на головному екрані
     *   2) Знайти кнопку у верхній панелі (іконку без тексту праворуч)
     *   3) Натиснути її
     *   4) Дочекатися повернення на екран логіну ("Вітаємо!")
     *
     *  ⚠️ Кнопка виходу не має text / content-desc, тому:
     *     - шукаємо всі клікабельні ViewGroup
     *     - фільтруємо ті, що знаходяться у верхній частині екрана (top ~ 60–150)
     *     - беремо останню як кнопку профілю/виходу
     */
    fun logoutToLogin(timeout: Long = 10_000L): HomeRobot {
        // 1) впевнюємось, що ми на головній
        assertHomeScreenVisible()

        // 2) знаходимо всі клікабельні ViewGroup
        val allClickable = device.findObjects(
            By.clazz("android.view.ViewGroup").clickable(true)
        )

        // 3) фільтруємо елементи у верхній панелі (по координаті top)
        val topBarCandidates = allClickable.filter {
            val top = it.visibleBounds.top
            top in 50..150 // верхня панель з іконками
        }

        // 4) беремо останній елемент у верхній панелі — праву іконку (профіль / вихід)
        val logoutCandidate = (topBarCandidates.lastOrNull() ?: allClickable.lastOrNull())
            ?: error("Не знайдено жодного клікабельного елемента у верхній панелі для виходу.")

        logoutCandidate.click()

        // 5) чекаємо, поки зʼявиться екран логіну ("Вітаємо!")
        val loginAppeared = device.wait(
            Until.hasObject(By.text(LOGIN_TITLE)),
            timeout
        )

        assertTrue(
            "Після натискання кнопки виходу екран логіну ('$LOGIN_TITLE') не зʼявився протягом $timeout мс.",
            loginAppeared
        )

        return this
    }
}
