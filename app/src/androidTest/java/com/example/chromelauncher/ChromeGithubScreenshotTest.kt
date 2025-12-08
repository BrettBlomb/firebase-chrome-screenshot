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

        Thread.sleep(2500)

        val possibleTexts = listOf(
            "No thanks",
            "No, thanks",
            "NOT NOW",
            "Not now",
            "Skip",
            "Dismiss",
            "Continue",
            "Got it",
            "Accept & continue",
            "Continue without an account"
        )

        // Try simple text selectors
        for (text in possibleTexts) {
            val button = device.findObject(UiSelector().textContains(text))
            if (button.exists()) {
                button.click()
                Thread.sleep(1500)
                return
            }
        }

        // WebView fallback selector
        val webButton = device.findObject(
            UiSelector().className("android.widget.Button").text("No thanks")
        )
        if (webButton.exists()) {
            webButton.click()
            Thread.sleep(1500)
            return
        }

        // Coordinate fallback
        val fallbackX = (device.displayWidth * 0.30).toInt()
        val fallbackY = (device.displayHeight * 0.80).toInt()

        device.click(fallbackX, fallbackY)
        Thread.sleep(1500)
    }

    @Test
    fun openChromeScrollRepoReadme() {

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val chromePkg = "com.android.chrome"

        // Your GitHub repo target URL
        val repoUrl = "https://github.com/BrettBlomb/firebase-chrome-screenshot/tree/master"

        // Initial Chrome launch intent (may not load URL until popup dismissed)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl)).apply {
            `package` = chromePkg
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(intent)

        val device = UiDevice.getInstance(instrumentation)

        // Wait for Chrome to be active
        device.wait(Until.hasObject(By.pkg(chromePkg).depth(0)), 20_000)

        // Handle Chrome popup
        dismissChromePopups(device)

        // 🔥 Force actual navigation AFTER popup is dismissed
        device.executeShellCommand(
            "am start -a android.intent.action.VIEW -d $repoUrl $chromePkg"
        )

        Thread.sleep(5000) // allow repo page to load

        // Scroll README slowly
        for (i in 1..6) {
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                device.displayWidth / 2,
                device.displayHeight / 4,
                30
            )
            Thread.sleep(1800)
        }

        // Save screenshot
        val directory = context.getExternalFilesDir("screenshots")!!
        directory.mkdirs()
        val screenshotFile = File(directory, "firebase_repo_scroll.png")

        device.takeScreenshot(screenshotFile)
        assert(screenshotFile.exists())
    }
}
