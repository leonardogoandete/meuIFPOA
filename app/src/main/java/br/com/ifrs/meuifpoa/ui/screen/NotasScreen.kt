package br.com.ifrs.meuifpoa.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.model.Nota
import br.com.ifrs.meuifpoa.ui.theme.MeuIFPOATheme
import br.com.ifrs.meuifpoa.ui.viewmodel.NotasViewModel

// 1. Stateful Composable (Connects to ViewModel)
@Composable
fun NotasScreen(notasViewModel: NotasViewModel = viewModel()) {
    val notas by notasViewModel.notas.collectAsState()

    // Trigger data loading when the screen is first composed
    LaunchedEffect(Unit) {
        notasViewModel.carregarNotas()
    }

    NotasScreenContent(notas = notas)
}

// 2. Stateless Composable (Receives data, easy to preview)
@Composable
fun NotasScreenContent(notas: List<Nota>?) {
    when {
        notas == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        notas.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Não há notas para exibir!")
            }
        }
        else -> {
            LazyColumn {
                items(notas) { nota ->
                    NotaItem(nota = nota)
                }
            }
        }
    }
}

// 3. Preview Functions
@Preview(showBackground = true, name = "Lista de Notas")
@Composable
fun NotasScreenPreview() {
    val sampleNotas = listOf(
        Nota(
            codigoDisciplina = "ADS123",
            nomeDisciplina = "Desenvolvimento para Dispositivos Móveis",
            primeiraUnidade = "9.5",
            segundaUnidade = "8.0",
            notaRecuperacao = "--",
            notaFinal = "8.8",
            numeroFaltas = "4",
            situacao = "Aprovado"
        ),
        Nota(
            codigoDisciplina = "ENG456",
            nomeDisciplina = "Engenharia de Software",
            primeiraUnidade = "5.0",
            segundaUnidade = "4.0",
            notaRecuperacao = "6.0",
            notaFinal = "5.0",
            numeroFaltas = "12",
            situacao = "Reprovado"
        )
    )

    MeuIFPOATheme {
        NotasScreenContent(notas = sampleNotas)
    }
}

@Preview(showBackground = true, name = "Sem Notas")
@Composable
fun NotasScreenEmptyPreview() {
    MeuIFPOATheme {
        NotasScreenContent(notas = emptyList())
    }
}

@Preview(showBackground = true, name = "Carregando")
@Composable
fun NotasScreenLoadingPreview() {
    MeuIFPOATheme {
        NotasScreenContent(notas = null)
    }
}
