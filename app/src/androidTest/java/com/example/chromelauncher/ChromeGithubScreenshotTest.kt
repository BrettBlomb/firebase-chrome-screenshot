package com.example.chromelauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.By
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ChromeGithubScreenshotTest {

    @Test
    fun openChromeAndScreenshotGithub() {

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = ApplicationProvider.getApplicationContext<Context>()

        val chromePkg = "com.android.chrome"

        // 1. Intent to launch Chrome and open your GitHub
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/BrettBlomb")
        ).apply {
            `package` = chromePkg
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        // 2. Launch Chrome
        context.startActivity(intent)

        val device = UiDevice.getInstance(instrumentation)

        // 3. Wait for Chrome to appear
        device.wait(
            Until.hasObject(By.pkg(chromePkg).depth(0)),
            20_000
        )

        // 4. Let the page finish loading
        Thread.sleep(5000)

        // 5. Save screenshot in /sdcard/.../files/screenshots/
        val directory = context.getExternalFilesDir("screenshots")!!
        directory.mkdirs()

        val screenshotFile = File(directory, "chrome_github_brettblomb.png")

        device.takeScreenshot(screenshotFile)

        // 6. Verify screenshot exists
        assert(screenshotFile.exists())
    }
}
