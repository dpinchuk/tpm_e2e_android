package com.example.tpm_e2e_android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.HomeRobot
import com.example.tpm_e2e_android.robots.LoginRobot
import androidx.test.uiautomator.By
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 🟦 Набір тестів авторизації у Fixator під різними ролями.
 */
@RunWith(AndroidJUnit4::class)
class FixatorLoginTest : BaseFixatorTest() {

    /**
     * 🟦 Допоміжний метод: логін під довільною парою email/пароль.
     */
    private fun loginWithCredentials(email: String, password: String) {
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(email)
            .typePassword(password)
            .tapLoginButton()
            .waitForHomeScreen()
    }

    @Test
    fun loginAsAdmin_shouldOpenHomeScreen() {
        loginWithCredentials(
            FixatorCredentials.ADMIN_LOGIN,
            FixatorCredentials.ADMIN_PASSWORD
        )
    }

    @Test
    fun loginAsUser_shouldOpenHomeScreen() {
        loginWithCredentials(
            FixatorCredentials.USER_LOGIN,
            FixatorCredentials.USER_PASSWORD
        )
    }

    @Test
    fun loginAsOfficial_shouldOpenHomeScreen() {
        loginWithCredentials(
            FixatorCredentials.OFFICIAL_LOGIN,
            FixatorCredentials.OFFICIAL_PASSWORD
        )
    }

    @Test
    fun loginAsClerk_shouldOpenHomeScreen() {
        loginWithCredentials(
            FixatorCredentials.CLERK_LOGIN,
            FixatorCredentials.CLERK_PASSWORD
        )
    }

    /**
     * 🟦 Після кожного тесту авторизації намагаємось виконати вихід.
     */
    @After
    fun logoutIfNeeded() {
        if (device.hasObject(By.text("Головна"))) {
            HomeRobot(device).logoutToLogin()
        }
    }
}
