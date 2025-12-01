package com.example.tpm_e2e_android.robots

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue

// 🟦 Таймаут очікування таблиці (мс)
private const val TABLE_TIMEOUT = 10_000L

/**
 * 🟦 Robot для роботи з модулем "Е-канцелярія" → "Вхідні документи".
 */
class EOfficeRobot(
    private val device: UiDevice,
) {

    /**
     * 🟦 Перевірити, що відкрито екран "Вхідні документи".
     *     Мінімальна умова:
     *       - є заголовок "Вхідні документи"
     *       - або будь-який текст, що починається з "Вхідні"
     */
    fun assertIncomingDocumentsScreenVisible(timeout: Long = TABLE_TIMEOUT): EOfficeRobot {
        val title = device.wait(
            Until.findObject(By.text("Вхідні документи")),
            timeout
        ) ?: device.wait(
            Until.findObject(By.textStartsWith("Вхідні")),
            timeout
        )

        assertTrue(
            "Екран 'Вхідні документи' не зʼявився протягом $timeout мс.",
            title != null
        )

        return this
    }

    /**
     * 🟦 Відкрити ПЕРШИЙ документ у таблиці.
     *
     * Без привʼязки до конкретних значень:
     *   1) Чекаємо, поки зʼявляться будь-які TextView.
     *   2) Беремо всі TextView.
     *   3) Фільтруємо тільки ті, що знаходяться у зоні таблиці:
     *        - досить праворуч (left ≥ 550) → правий блок, не ліве меню
     *        - нижче заголовка/хедера (top ≥ 500)
     *   4) Обираємо елемент з мінімальним top → НАЙВЕРХНІШИЙ у таблиці.
     *   5) Клікаємо по ньому.
     */
    fun openFirstIncomingDocument(): EOfficeRobot {
        // 1) чекаємо хоч один TextView
        device.wait(
            Until.hasObject(By.clazz("android.widget.TextView")),
            TABLE_TIMEOUT,
        )

        // 2) всі TextView
        val allTextViews = device.findObjects(By.clazz("android.widget.TextView"))

        // 3) кандидати в зоні таблиці
        val candidates = allTextViews.filter { view ->
            val b = view.visibleBounds

            val inRightArea = b.left >= 550   // зона таблиці праворуч від меню
            val belowHeader = b.top >= 500    // нижче шапки сторінки / заголовків

            inRightArea && belowHeader
        }

        check(candidates.isNotEmpty()) {
            "Не знайдено жодної комірки в зоні таблиці (TextView з left ≥ 550 та top ≥ 500). " +
                    "Переконайся, що таблиця з документами завантажена."
        }

        // 4) найверхніший елемент за координатою top
        val topCell = candidates.minByOrNull { it.visibleBounds.top }
            ?: error("Не вдалося визначити верхній елемент у таблиці вхідних документів.")

        // 5) клікаємо по ньому
        topCell.click()

        return this
    }
}
