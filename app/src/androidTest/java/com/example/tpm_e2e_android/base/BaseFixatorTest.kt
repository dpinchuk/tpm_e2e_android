package com.example.tpm_e2e_android.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before

private const val APP_PACKAGE = "ua.com.fixator.app"
private const val MAIN_ACTIVITY = "crc641e3e720e8099bf3e.MainActivity"
private const val LAUNCH_TIMEOUT = 40_000L

abstract class BaseFixatorTest {

    protected val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUpFixatorApp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context: Context = instrumentation.context
        val pm: PackageManager = context.packageManager

        // ✅ 1) Clear app data – as if it was just installed (user will be logged out)
        try {
            val clearResult = device.executeShellCommand("pm clear $APP_PACKAGE")
            println("DEBUG: pm clear $APP_PACKAGE -> $clearResult")
        } catch (e: Exception) {
            println("WARN: Failed to clear app data for $APP_PACKAGE: ${e.message}")
        }

        // ✅ 2) Start main activity
        device.pressHome()

        val launchIntentFromPm = pm.getLaunchIntentForPackage(APP_PACKAGE)

        val launchIntent = (launchIntentFromPm ?: Intent().apply {
            setClassName(APP_PACKAGE, MAIN_ACTIVITY)
        }).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(launchIntent)

        // ✅ 3) Wait for any UI from this package
        val appeared = device.wait(
            Until.hasObject(By.pkg(APP_PACKAGE)),
            LAUNCH_TIMEOUT
        )

        assertTrue(
            "Fixator app did not open within $LAUNCH_TIMEOUT ms. " +
                    "Check MAIN_ACTIVITY or first screen behavior.",
            appeared
        )
    }

    @After
    fun tearDownFixatorApp() {
        try {
            val stopResult = device.executeShellCommand("am force-stop $APP_PACKAGE")
            println("DEBUG: am force-stop $APP_PACKAGE -> $stopResult")
        } catch (e: Exception) {
            println("WARN: Failed to force-stop $APP_PACKAGE: ${e.message}")
        }
    }
}
