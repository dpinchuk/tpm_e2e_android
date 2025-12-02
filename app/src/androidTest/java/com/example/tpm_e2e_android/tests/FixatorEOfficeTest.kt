package com.example.tpm_e2e_android.tests

import HomeRobot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.EOfficeRobot
import com.example.tpm_e2e_android.robots.LoginRobot
import com.example.tpm_e2e_android.robots.SideMenuRobot
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 🟦 Тести роботи з модулем Е-канцелярії.
 */
@RunWith(AndroidJUnit4::class)
class FixatorEOfficeTest : BaseFixatorTest() {

    @Test
    fun adminCanOpenFirstIncomingDocument() {
        // 🔹 Логін під адміном
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(FixatorCredentials.ADMIN_LOGIN)
            .typePassword(FixatorCredentials.ADMIN_PASSWORD)
            .tapLoginButton()

        val homeRobot = HomeRobot(device)
        homeRobot.handlePossibleStartupDialogs()
        homeRobot.assertHomeScreenVisible()

        // 🔹 Переходимо в Е-канцелярію → Вхідні документи
        SideMenuRobot(device)
            .openEOffice()
            .openIncomingDocuments()

        // 🔹 Перевіряємо екран та відкриваємо перший вхідний документ
        EOfficeRobot(device)
            .assertIncomingDocumentsScreenVisible()
            .openFirstIncomingDocument()
    }

    @Test
    fun clerkCanSeeOutgoingDocumentsList() {
        // Login as Clerk
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(FixatorCredentials.CLERK_LOGIN)
            .typePassword(FixatorCredentials.CLERK_PASSWORD)
            .tapLoginButton()

        HomeRobot(device)
            .assertHomeScreenVisible()

        // Navigate to E-Office → Outgoing documents
        SideMenuRobot(device)
            .openEOffice()
            .openOutgoingDocuments()

        // Verify outgoing documents screen and list
        EOfficeRobot(device)
            .assertOutgoingDocumentsScreenVisible()
            .assertOutgoingDocumentsListNotEmpty()
    }


}
