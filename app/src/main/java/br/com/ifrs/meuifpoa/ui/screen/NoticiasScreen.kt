package br.com.ifrs.meuifpoa.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasViewModel

@Composable
fun NoticiasScreen(noticiasViewModel: NoticiasViewModel = viewModel()) {
    val uiState by noticiasViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        noticiasViewModel.loadDataForCategory()
    }

    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        noticiasViewModel.clearError()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        CategoryTabs( // Replaced SegmentedButton with custom tabs
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { newCategory ->
                noticiasViewModel.onCategoryChange(newCategory)
            }
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { noticiasViewModel.onSearchQueryChange(it) },
            label = { Text("Buscar em ${if (uiState.selectedCategory == "noticia") "Notícias" else "Editais"}") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val hasContent = uiState.noticias.isNotEmpty() || uiState.editais.isNotEmpty()
            if (!hasContent) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum item encontrado.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                    if (uiState.selectedCategory == "noticia") {
                        items(uiState.noticias) { noticia ->
                            NoticiaItem(noticia = noticia)
                        }
                    } else {
                        items(uiState.editais) { edital ->
                            EditalItem(edital = edital)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        CategoryButton(text = "Notícias", isSelected = selectedCategory == "noticia") {
            onCategorySelected("noticia")
        }
        CategoryButton(text = "Editais", isSelected = selectedCategory == "edital") {
            onCategorySelected("edital")
        }
    }
}

@Composable
private fun CategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val underlineSize by animateDpAsState(targetValue = if (isSelected) 2.dp else 0.dp, label = "underlineAnimation")

    TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, color = textColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier
                .height(underlineSize)
                .width(40.dp)
                .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
