package com.example.tpm_e2e_android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// 🟦 Пакет тестованого застосунку Fixator (new)
private const val APP_PACKAGE = "ua.com.fixator.app"

// 🟦 Повна назва головної Activity (з AndroidManifest apk)
private const val MAIN_ACTIVITY = "crc641e3e720e8099bf3e.MainActivity"

// 🟦 Таймаут очікування появи головного екрана (у мілісекундах)
private const val LAUNCH_TIMEOUT = 20_000L // 20 секунд, щоб дати більше часу на старт

@RunWith(AndroidJUnit4::class)
class FixatorLaunchTest {

    // 🟦 Пристрій, з яким працюють UI-тести (емулятор або реальний девайс)
    private val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        // 🟦 Повернутися на головний екран Android
        device.pressHome()

        // 🟦 Сформувати повну компоненту Activity: <package>/<activity>
        val componentName = "$APP_PACKAGE/$MAIN_ACTIVITY"

        // 🟦 Запустити застосунок Fixator через shell-команду `am start`
        device.executeShellCommand("am start -n $componentName")

        // 🟦 Дочекатися, поки на екрані зʼявиться хоч один елемент з package Fixator
        val launched = device.wait(
            Until.hasObject(By.pkg(APP_PACKAGE)),
            LAUNCH_TIMEOUT
        )

        // 🟦 Якщо за таймаут UI не зʼявився — тест падає тут, з чітким повідомленням
        assertTrue(
            "Не вдалося дочекатися запуску Fixator протягом $LAUNCH_TIMEOUT мс.",
            launched
        )
    }

    @Test
    fun appLaunchesSuccessfully() {
        // 🟦 На цьому етапі ми вже впевнені, що щось з package Fixator є на екрані.
        //     Знайдемо кореневий елемент (будь-який view з цим package) та перевіримо, що він існує.
        val rootObject = device.findObject(By.pkg(APP_PACKAGE))

        // 🟦 Базова перевірка: UI застосунку дійсно завантажився
        assertNotNull("Головний екран Fixator не завантажився або недоступний.", rootObject)
    }
}
