package br.com.ifrs.meuifpoa.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.ifrs.meuifpoa.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun IntegralizacaoChart(progress: Int) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            PieChart(context).apply {
                // Configurações visuais que não mudam
                description.isEnabled = false
                legend.isEnabled = false
                isRotationEnabled = false
                holeRadius = 80f
                setTouchEnabled(false)
                setDrawEntryLabels(false)
                setUsePercentValues(false)
                setCenterTextColor(Color.Black.toArgb())
                setCenterTextSize(20f)
            }
        },
        update = { chart ->
            val realizado = progress.toFloat()
            val restante = 100f - realizado

            val entries = listOf(
                PieEntry(realizado, ""),
                PieEntry(restante, "")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    android.graphics.Color.rgb(47, 158, 65), // #2F9E41 Aprovado
                    android.graphics.Color.rgb(207, 33, 45)  // #CF212D Reprovado/Restante
                )
                setDrawValues(false)
            }

            chart.data = PieData(dataSet)
            chart.centerText = "Integralizado\n$progress%"
            chart.invalidate()
        }
    )
}
