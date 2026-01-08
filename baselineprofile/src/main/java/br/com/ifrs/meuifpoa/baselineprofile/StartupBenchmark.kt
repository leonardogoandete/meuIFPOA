package br.com.ifrs.meuifpoa.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark: 
 * 1) Switch to a release build variant in the IDE (as our profile rules are configured to generate only for release builds).
 * 2) Run the installation task: `../gradlew :app:installRelease`
 * 3) Run the benchmark on a physical device or emulator with API < 33.
 * 4) If you get "Could not find context for passed-in package", run `adb shell pm clear br.com.ifrs.meuifpoa` and try again.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "br.com.ifrs.meuifpoa",
        metrics = listOf(androidx.benchmark.macro.StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Full(),
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }
}
