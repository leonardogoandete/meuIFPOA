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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NotasViewModel

@Composable
fun NotasScreen(notasViewModel: NotasViewModel = viewModel()) {
    val notas by notasViewModel.notas.collectAsState()

    LaunchedEffect(Unit) {
        notasViewModel.carregarNotas()
    }

    if (notas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(notas) { nota ->
                NotaItem(nota = nota)
            }
        }
    }
}
