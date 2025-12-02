package com.example.tpm_e2e_android.tests

import HomeRobot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.example.tpm_e2e_android.base.BaseFixatorTest
import com.example.tpm_e2e_android.constants.FixatorCredentials
import com.example.tpm_e2e_android.robots.LoginRobot
import com.example.tpm_e2e_android.robots.SideMenuRobot
import com.example.tpm_e2e_android.robots.TasksRobot
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Checks that Clerk can open "Задачі" screen and see non-empty tasks list.
 */
@RunWith(AndroidJUnit4::class)
class FixatorTasksTest : BaseFixatorTest() {

    @Test
    fun clerkCanSeeTasksList() {
        // Login as Clerk
        LoginRobot(device)
            .assertLoginScreenVisible()
            .typeEmail(FixatorCredentials.CLERK_LOGIN)
            .typePassword(FixatorCredentials.CLERK_PASSWORD)
            .tapLoginButton()

        // Wait for home screen
        HomeRobot(device)
            .assertHomeScreenVisible()

        // Open "Задачі" from side menu
        SideMenuRobot(device)
            .openTasks()

        // Verify tasks screen and list
        TasksRobot(device)
            .assertTasksScreenVisible()
            .assertTasksListNotEmpty()
    }

    @After
    fun logoutIfNeeded() {
        if (device.hasObject(By.text("Е-канцелярія")) ||
            device.hasObject(By.text("Задачі"))
        ) {
            HomeRobot(device).logoutToLogin()
        }
    }
}
