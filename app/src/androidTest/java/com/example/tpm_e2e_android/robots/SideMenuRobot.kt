package com.example.tpm_e2e_android.robots

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

// 🟦 Текст пунктів меню з лівої панелі
private const val E_OFFICE_MENU_TEXT = "Е-канцелярія"
private const val INCOMING_DOCS_MENU_TEXT = "Вхідні документи"

/**
 * 🟦 Robot для роботи з лівою боковою панеллю (меню) Fixator.
 */
class SideMenuRobot(
    private val device: UiDevice
) {

    /**
     * 🟦 Клік по пункту "Е-канцелярія" у лівому меню.
     */
    fun openEOffice(timeout: Long = 10_000L): SideMenuRobot {
        val appeared = device.wait(
            Until.hasObject(By.text(E_OFFICE_MENU_TEXT)),
            timeout
        )

        assertTrue(
            "Пункт меню '$E_OFFICE_MENU_TEXT' не зʼявився протягом $timeout мс.",
            appeared
        )

        val menuItem = device.findObject(By.text(E_OFFICE_MENU_TEXT))
        assertNotNull("Елемент меню '$E_OFFICE_MENU_TEXT' не знайдено для кліку.", menuItem)

        menuItem.click()
        return this
    }

    /**
     * 🟦 Клік по підпункту "Вхідні документи" в блоці "Е-канцелярія".
     *     Викликати ПІСЛЯ openEOffice(), щоб підменю було розкрито.
     */
    fun openIncomingDocuments(timeout: Long = 10_000L): SideMenuRobot {
        val appeared = device.wait(
            Until.hasObject(By.text(INCOMING_DOCS_MENU_TEXT)),
            timeout
        )

        assertTrue(
            "Підпункт меню '$INCOMING_DOCS_MENU_TEXT' не зʼявився протягом $timeout мс.",
            appeared
        )

        val item = device.findObject(By.text(INCOMING_DOCS_MENU_TEXT))
        assertNotNull("Елемент меню '$INCOMING_DOCS_MENU_TEXT' не знайдено для кліку.", item)

        item.click()
        return this
    }
}
