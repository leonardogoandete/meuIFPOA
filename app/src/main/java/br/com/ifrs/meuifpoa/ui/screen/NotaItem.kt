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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ifrs.meuifpoa.model.Nota

@Composable
fun NotaItem(nota: Nota) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(getBackgroundColor(nota.situacao))
                .padding(16.dp)
        ) {
            Text(
                text = nota.codigoDisciplina ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = nota.nomeDisciplina ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NotaValor(titulo = "Unidade 1", valor = nota.primeiraUnidade)
                NotaValor(titulo = "Unidade 2", valor = nota.segundaUnidade)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NotaValor(titulo = "Recuperação", valor = nota.notaRecuperacao)
                NotaValor(titulo = "Resultado", valor = nota.notaFinal, backgroundColor = getResultadoBackgroundColor(nota.situacao))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Faltas: ${nota.numeroFaltas ?: "--"}", fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Situação: ${nota.situacao ?: "--"}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NotaValor(titulo: String, valor: String?, backgroundColor: Color = Color.White) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Text(text = titulo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = valor.takeIf { !it.isNullOrEmpty() } ?: "--", fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}

fun getBackgroundColor(situacao: String?): Color {
    return when {
        situacao.equals("aprovado", ignoreCase = true) -> Color(0xFF2F9E41)
        situacao == "--" -> Color.Gray
        else -> Color(0xFFCF212D)
    }
}

fun getResultadoBackgroundColor(situacao: String?): Color {
    return when {
        situacao.equals("aprovado", ignoreCase = true) -> Color(0xFF2F9E41)
        situacao == "--" -> Color.Transparent
        else -> Color(0xFFCF212D)
    }
}
