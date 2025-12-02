package com.example.tpm_e2e_android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.example.tpm_e2e_android.base.BaseFixatorTest
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

private const val APP_PACKAGE = "ua.com.fixator.app"
private const val UI_TIMEOUT = 20_000L

/**
 * 🟦 Перевірка, що застосунок Fixator успішно стартує.
 */
@RunWith(AndroidJUnit4::class)
class FixatorLaunchTest : BaseFixatorTest() {

    @Test
    fun appLaunchesSuccessfully() {
        // 🔹 Просто чекаємо будь-який об’єкт з нашим пакетом
        val root = device.wait(
            Until.findObject(By.pkg(APP_PACKAGE)),
            UI_TIMEOUT
        )

        assertNotNull(
            "Головний екран Fixator не завантажився або недоступний після запуску.",
            root
        )
    }
}
