package com.example.chromelauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ChromeGithubScreenshotTest {

    private fun dismissChromePopups(device: UiDevice) {
        val selectors = listOf("No thanks", "NO THANKS", "Skip", "Not now")

        for (text in selectors) {
            val button = device.findObject(UiSelector().textContains(text))
            if (button.exists()) {
                button.click()
                Thread.sleep(1200)
            }
        }
    }

    @Test
    fun openChromeScrollRepoReadme() {

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val chromePkg = "com.android.chrome"

        // Open repo directly
        val repoUrl = "https://github.com/BrettBlomb/firebase-chrome-screenshot/tree/master"

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(repoUrl)
        ).apply {
            `package` = chromePkg
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(intent)

        val device = UiDevice.getInstance(instrumentation)

        // Wait for Chrome
        device.wait(Until.hasObject(By.pkg(chromePkg).depth(0)), 20_000)

        // Give time for popup to appear
        Thread.sleep(2500)
        dismissChromePopups(device)

        // Wait for GitHub repo page load
        Thread.sleep(5000)

        // Now scroll down the README slowly
        for (i in 1..6) {  // Adjust number of scrolls here
            device.swipe(
                device.displayWidth / 2,                 // startX
                device.displayHeight * 3 / 4,            // startY (lower)
                device.displayWidth / 2,                 // endX
                device.displayHeight / 4,                // endY (upper)
                30                                       // steps (higher = slower)
            )
            Thread.sleep(1800)  // Delay between scrolls
        }

        // Screenshot at bottom (optional)
        val directory = context.getExternalFilesDir("screenshots")!!
        directory.mkdirs()
        val screenshotFile = File(directory, "firebase_repo_scroll.png")

        device.takeScreenshot(screenshotFile)

        assert(screenshotFile.exists())
    }
}
