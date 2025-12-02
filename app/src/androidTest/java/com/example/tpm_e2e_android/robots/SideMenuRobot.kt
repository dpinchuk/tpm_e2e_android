package com.example.tpm_e2e_android.robots

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.fail

class SideMenuRobot(
    private val device: UiDevice = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )
) {

    // If you know real ids/desc - put them here
    private val MENU_BUTTON_DESC = "Open navigation drawer"
    private val MENU_BUTTON_TEXT = "Меню"
    private val EOFFICE_MENU_TEXT = "Е-канцелярія"
    private val INCOMING_DOCS_MENU_TEXT = "Вхідні документи"

    /**
     * Open side menu (navigation drawer) and wait until it is visible.
     * If no explicit side menu button is found, we just log and continue
     * (home screen may already contain E-Office entry directly).
     */
    fun openSideMenu(timeoutMs: Long = 60_000): SideMenuRobot {
        var menuButton: UiObject2? = null

        // 1) Try by generic 'menu' content-desc (hamburger)
        menuButton = device.findObject(By.descContains("menu"))

        // 2) Try known description
        if (menuButton == null && MENU_BUTTON_DESC.isNotBlank()) {
            menuButton = device.findObject(By.descContains(MENU_BUTTON_DESC))
        }

        // 3) Fallback: by text "Меню"
        if (menuButton == null) {
            menuButton = device.findObject(By.textContains(MENU_BUTTON_TEXT))
        }

        // 4) Fallback: try first ImageButton (typical nav-drawer icon)
        if (menuButton == null) {
            val imageButtons = device.findObjects(By.clazz("android.widget.ImageButton"))

            val candidate = imageButtons.firstOrNull { btn ->
                (btn.isClickable) && (btn.contentDescription != null)
            } ?: imageButtons.firstOrNull { btn ->
                btn.isClickable
            }

            if (candidate != null) {
                menuButton = candidate
            }
        }

        // ❗ Если кнопки реально нет – НЕ падаем, просто логируем и идём дальше.
        if (menuButton == null) {
            val clickables = device.findObjects(By.clickable(true))
                .take(10)
                .joinToString("\n") { obj ->
                    "CLICKABLE: class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}', desc='${obj.contentDescription}'"
                }

            val imageButtons = device.findObjects(By.clazz("android.widget.ImageButton"))
                .take(10)
                .joinToString("\n") { obj ->
                    "IMAGEBUTTON: class=${obj.className}, text='${obj.text}', resId='${obj.resourceName}', desc='${obj.contentDescription}'"
                }

            println(
                "WARN: Side menu button was not found. " +
                        "Tried content-desc, text and ImageButton heuristic.\n" +
                        "Clickable elements:\n$clickables\n" +
                        "ImageButtons:\n$imageButtons\n" +
                        "Continuing without opening side menu – E-Office entry may be on home screen."
            )

            return this
        }

        // Если кнопка всё-таки нашлась – кликаем и ждём появления текстов меню
        menuButton!!.click()

        val opened = device.wait(
            Until.hasObject(
                By.clazz("android.widget.TextView")
            ),
            timeoutMs
        )

        if (!opened) {
            println(
                "WARN: Side menu did not appear within $timeoutMs ms after clicking menu button. " +
                        "Continuing anyway – maybe UI layout is different."
            )
        }

        return this
    }

    /**
     * Open E-Office section from side menu.
     */
    fun openEOffice(timeoutMs: Long = 60_000): SideMenuRobot {
        openSideMenu(timeoutMs)

        // Wait for any menu texts to load
        device.wait(
            Until.hasObject(By.clazz("android.widget.TextView")),
            timeoutMs
        )

        var eofficeItem: UiObject2? = null

        // 1) Try exact known text
        if (EOFFICE_MENU_TEXT.isNotBlank()) {
            eofficeItem = device.findObject(By.textContains(EOFFICE_MENU_TEXT))
        }

        // 2) Fallback: any text containing "канцел"
        if (eofficeItem == null) {
            val candidates = device.findObjects(By.clazz("android.widget.TextView"))
            eofficeItem = candidates.firstOrNull { obj ->
                val t = obj.text ?: ""
                t.contains("канцел", ignoreCase = true)
            }
        }

        if (eofficeItem == null) {
            val texts = device.findObjects(By.clazz("android.widget.TextView"))
                .take(15)
                .joinToString("\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "E-Office menu item was not found in side menu. " +
                        "Tried '$EOFFICE_MENU_TEXT' and contains('канцел'). " +
                        "Visible TextViews:\n$texts"
            )
        }

        eofficeItem!!.click()
        return this
    }

    /**
     * Inside E-Office, open "Incoming documents" section (from tabs or additional menu).
     */
    fun openIncomingDocuments(timeoutMs: Long = 60_000): SideMenuRobot {
        // Wait for E-Office screen to appear (any text containing 'канцел' or 'Вхідн')
        device.wait(
            Until.hasObject(
                By.textContains("канцел")
            ),
            timeoutMs
        )

        var incomingItem: UiObject2? = null

        // 1) Try exact text
        if (INCOMING_DOCS_MENU_TEXT.isNotBlank()) {
            incomingItem = device.findObject(By.textContains(INCOMING_DOCS_MENU_TEXT))
        }

        // 2) Fallback: any text containing "Вхідн"
        if (incomingItem == null) {
            val candidates = device.findObjects(By.clazz("android.widget.TextView"))
            incomingItem = candidates.firstOrNull { obj ->
                val t = obj.text ?: ""
                t.contains("Вхідн", ignoreCase = true)
            }
        }

        if (incomingItem == null) {
            val texts = device.findObjects(By.clazz("android.widget.TextView"))
                .take(15)
                .joinToString("\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "Incoming documents menu/tab item was not found. " +
                        "Tried '$INCOMING_DOCS_MENU_TEXT' and contains('Вхідн'). " +
                        "Visible TextViews:\n$texts"
            )
        }

        incomingItem!!.click()
        return this
    }

    fun openOutgoingDocuments(timeoutMs: Long = 25_000): SideMenuRobot {
        // Wait until E-Office screen / menu is visible
        device.wait(
            Until.hasObject(By.textContains("Е-канцел")),
            timeoutMs
        )

        var outgoingItem: UiObject2? = null

        // 1) Try exact text "Вихідні документи"
        outgoingItem = device.findObject(By.textContains("Вихідні документи"))

        // 2) Fallback: any text containing "Вихідн"
        if (outgoingItem == null) {
            val candidates = device.findObjects(By.clazz("android.widget.TextView"))
            outgoingItem = candidates.firstOrNull { obj ->
                val t = obj.text ?: ""
                t.contains("Вихідн", ignoreCase = true)
            }
        }

        if (outgoingItem == null) {
            val texts = device.findObjects(By.clazz("android.widget.TextView"))
                .take(15)
                .joinToString("\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "Outgoing documents menu item was not found. " +
                        "Tried text 'Вихідні документи' and contains('Вихідн'). " +
                        "Visible TextViews:\n$texts"
            )
        }

        outgoingItem!!.click()
        return this
    }

    fun openTasks(timeoutMs: Long = 25_000): SideMenuRobot {
        // Side menu may already be visible; if there is a menu button, try to use it
        openSideMenu(timeoutMs)

        device.wait(
            Until.hasObject(By.clazz("android.widget.TextView")),
            timeoutMs
        )

        var tasksItem: UiObject2? = null

        // 1) Exact text "Задачі"
        tasksItem = device.findObject(By.textContains("Задачі"))

        // 2) Fallback: any text containing "Задач"
        if (tasksItem == null) {
            val candidates = device.findObjects(By.clazz("android.widget.TextView"))
            tasksItem = candidates.firstOrNull { obj ->
                val t = obj.text ?: ""
                t.contains("Задач", ignoreCase = true)
            }
        }

        if (tasksItem == null) {
            val texts = device.findObjects(By.clazz("android.widget.TextView"))
                .take(20)
                .joinToString("\n") { obj ->
                    "text='${obj.text}', resId='${obj.resourceName}'"
                }

            fail(
                "Tasks menu item was not found in side menu. " +
                        "Tried 'Задачі' and contains('Задач'). Visible TextViews:\n$texts"
            )
        }

        tasksItem!!.click()
        return this
    }


}
