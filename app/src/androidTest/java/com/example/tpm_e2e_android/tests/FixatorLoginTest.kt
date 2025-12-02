package com.example.tpm_e2e_android.tests

import HomeRobot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.LoginRobot
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 🟦 Набір тестів авторизації у Fixator під різними ролями.
 */
@RunWith(AndroidJUnit4::class)
class FixatorLoginTest : BaseFixatorTest() {

    /**
     * 🔹 Уніфікований сценарій логіну та очікування домашнього екрана.
     */
    private fun loginWithCredentials(email: String, password: String) {
        val loginRobot = LoginRobot(device)

        loginRobot
            .assertLoginScreenVisible()
            .typeEmail(email)
            .typePassword(password)
            .tapLoginButton()

        // 🔹 Обробка можливих діалогів (onboarding / permissions) – всередині HomeRobot
        val homeRobot = HomeRobot(device)
        homeRobot.handlePossibleStartupDialogs()
        homeRobot.assertHomeScreenVisible()
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

}
