package br.com.ifrs.meuifpoa.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.model.Nota

@Composable
fun NotaItem(nota: Nota) {
    val statusColor = when {
        nota.situacao.equals("aprovado", ignoreCase = true) -> Color(0xFF2E7D32) // Dark Green
        nota.situacao == "--" || nota.situacao.isNullOrEmpty() -> Color(0xFF616161) // Gray
        else -> Color(0xFFC62828) // Dark Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nota.codigoDisciplina ?: "",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = nota.nomeDisciplina ?: stringResource(R.string.discipline_not_informed),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradeBox(title = stringResource(R.string.unit_1_label), grade = nota.primeiraUnidade, modifier = Modifier.weight(1f))
                GradeBox(title = stringResource(R.string.unit_2_label), grade = nota.segundaUnidade, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradeBox(title = stringResource(R.string.recovery_label), grade = nota.notaRecuperacao, modifier = Modifier.weight(1f))
                GradeBox(
                    title = stringResource(R.string.result_label),
                    grade = nota.notaFinal,
                    modifier = Modifier.weight(1f),
                    // Only color the result box if the status is final
                    backgroundColor = if (nota.situacao != "--" && !nota.situacao.isNullOrEmpty()) statusColor.copy(alpha = 0.8f) else Color.White,
                    textColor = if (nota.situacao != "--" && !nota.situacao.isNullOrEmpty()) Color.White else Color.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoText(label = stringResource(R.string.absences_label), value = nota.numeroFaltas ?: "--")
                InfoText(label = stringResource(R.string.situation_label), value = nota.situacao ?: "--")
            }
        }
    }
}

@Composable
private fun GradeBox(
    title: String,
    grade: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black
) {
    Column(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = 0.8f)
        )
        Text(
            text = grade?.takeIf { it.isNotEmpty() } ?: "--",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun InfoText(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
