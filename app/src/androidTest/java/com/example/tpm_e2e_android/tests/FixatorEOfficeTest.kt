package com.example.tpm_e2e_android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.EOfficeRobot
import com.example.tpm_e2e_android.robots.HomeRobot
import com.example.tpm_e2e_android.robots.LoginRobot
import com.example.tpm_e2e_android.robots.SideMenuRobot
import androidx.test.uiautomator.By
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixatorEOfficeTest : BaseFixatorTest() {

    @Test
    fun adminCanOpenFirstIncomingDocument() {
        // 🟦 Логін під ADMIN
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(FixatorCredentials.ADMIN_LOGIN)
            .typePassword(FixatorCredentials.ADMIN_PASSWORD)
            .tapLoginButton()
            .waitForHomeScreen()

        // 🟦 Ліве меню: Е-канцелярія → Вхідні документи
        SideMenuRobot(device)
            .openEOffice()
            .openIncomingDocuments()

        // 🟦 Екран "Вхідні документи" + клік по першому запису
        EOfficeRobot(device)
            .assertIncomingDocumentsScreenVisible()
            .openFirstIncomingDocument()
    }

    @After
    fun logoutIfNeeded() {
        if (device.hasObject(By.text("Головна"))) {
            HomeRobot(device).logoutToLogin()
        }
    }
}
