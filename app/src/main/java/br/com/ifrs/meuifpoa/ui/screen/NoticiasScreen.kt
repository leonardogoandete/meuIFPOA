package br.com.ifrs.meuifpoa.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasViewModel

@Composable
fun NoticiasScreen(noticiasViewModel: NoticiasViewModel = viewModel()) {
    val uiState by noticiasViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Trigger data loading when the screen is first composed
    LaunchedEffect(Unit) {
        noticiasViewModel.carregarNoticias()
    }

    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        noticiasViewModel.clearError()
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { noticiasViewModel.onSearchQueryChange(it) },
            label = { Text("Buscar notícias") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(uiState.noticias) { noticia ->
                    NoticiaItem(noticia = noticia)
                }
            }
        }
    }
}
