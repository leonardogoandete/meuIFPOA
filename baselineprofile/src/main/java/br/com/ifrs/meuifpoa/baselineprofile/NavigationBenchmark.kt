package br.com.ifrs.meuifpoa.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupAndNavigation() = benchmarkRule.measureRepeated(
        packageName = "br.com.ifrs.meuifpoa",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
        setupBlock = {
            pressHome()
        }
    ) {
        // Startup
        startActivityAndWait()

        // Navega para a tela de notas
        device.findObject(By.res("Notas"))?.click()
        device.wait(Until.hasObject(By.res("NotasScreen")), 10_000)

        // Volta para a tela inicial
        device.pressBack()
        device.wait(Until.hasObject(By.res("HomeScreen")), 10_000)
    }
}
