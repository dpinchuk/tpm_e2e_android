package com.example.tpm_e2e_android.base

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before

// 🟦 Пакет тестованого застосунку Fixator (new)
const val FIXATOR_APP_PACKAGE = "ua.com.fixator.app"

// 🟦 Повна назва головної Activity (з AndroidManifest apk)
private const val FIXATOR_MAIN_ACTIVITY = "crc641e3e720e8099bf3e.MainActivity"

// 🟦 Таймаут очікування появи головного екрана (у мілісекундах)
private const val FIXATOR_LAUNCH_TIMEOUT = 20_000L

/**
 * 🟦 Базовий клас для всіх UI-тестів Fixator.
 *     Відповідає за запуск застосунку перед кожним тестом.
 */
open class BaseFixatorTest {

    // 🟦 Пристрій, з яким працюють UI-тести (емулятор або реальний девайс)
    protected val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUpFixatorApp() {
        // 🟦 Повернутися на головний екран Android
        device.pressHome()

        // 🟦 Сформувати повну компоненту Activity: <package>/<activity>
        val componentName = "$FIXATOR_APP_PACKAGE/$FIXATOR_MAIN_ACTIVITY"

        // 🟦 Запустити застосунок Fixator через shell-команду `am start`
        device.executeShellCommand("am start -n $componentName")

        // 🟦 Дочекатися, поки на екрані зʼявиться хоч один елемент із package Fixator
        val launched = device.wait(
            Until.hasObject(By.pkg(FIXATOR_APP_PACKAGE)),
            FIXATOR_LAUNCH_TIMEOUT
        )

        // 🟦 Якщо запуск не вдався — кидаємо AssertionError (тест упаде одразу)
        check(launched) {
            "Не вдалося запустити Fixator протягом $FIXATOR_LAUNCH_TIMEOUT мс."
        }
    }
}
