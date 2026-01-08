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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.model.Edital
import br.com.ifrs.meuifpoa.model.Noticia
import br.com.ifrs.meuifpoa.ui.theme.MeuIFPOATheme
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasUiState
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasViewModel

@Composable
fun NoticiasScreen(noticiasViewModel: NoticiasViewModel = viewModel()) {
    val uiState by noticiasViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.selectedCategory) { // Re-trigger when category changes
        noticiasViewModel.loadDataForCategory()
    }

    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        noticiasViewModel.clearError()
    }

    NoticiasScreenContent(uiState = uiState, onCategoryChange = {
        noticiasViewModel.onCategoryChange(it)
    }, onSearchQueryChange = {
        noticiasViewModel.onSearchQueryChange(it)
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticiasScreenContent(
    uiState: NoticiasUiState,
    onCategoryChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        CategoryTabs(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = onCategoryChange
        )

        Spacer(Modifier.height(2.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(if (uiState.selectedCategory == "noticia") stringResource(R.string.search_in_news_label) else stringResource(R.string.search_in_announcements_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(2.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val hasContent = uiState.noticias.isNotEmpty() || uiState.editais.isNotEmpty()
            if (!hasContent) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_items_found_message))
                }
            } else {
                LazyColumn(modifier = Modifier.padding(top = 4.dp)) {
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
        CategoryButton(text = stringResource(R.string.news_category_button), isSelected = selectedCategory == "noticia") {
            onCategorySelected("noticia")
        }
        CategoryButton(text = stringResource(R.string.announcements_category_button), isSelected = selectedCategory == "edital") {
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
                .width(70.dp)
                .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Notícias com Conteúdo")
@Composable
fun NoticiasScreenWithContentPreview() {
    val sampleNoticias = listOf(
        Noticia(1, "/link1", "IFRS abre 500 vagas para cursos", "Resumo da notícia sobre vagas abertas...", "01/01/26", "10:00"),
        Noticia(2, "/link2", "Campus Porto Alegre realiza evento", "Resumo do evento de tecnologia...", "02/01/26", "11:00")
    )
    MeuIFPOATheme {
        NoticiasScreenContent(
            uiState = NoticiasUiState(noticias = sampleNoticias, selectedCategory = "noticia"),
            onCategoryChange = {},
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Editais com Conteúdo")
@Composable
fun EditaisScreenWithContentPreview() {
    val sampleEditais = listOf(
        Edital(1, "/link1", "Edital 01/2026 - Monitoria", "03/01/2026"),
        Edital(2, "/link2", "Edital 02/2026 - Bolsas", "04/01/2026")
    )
    MeuIFPOATheme {
        NoticiasScreenContent(
            uiState = NoticiasUiState(editais = sampleEditais, selectedCategory = "edital"),
            onCategoryChange = {},
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Tela Carregando")
@Composable
fun NoticiasScreenLoadingPreview() {
    MeuIFPOATheme {
        NoticiasScreenContent(
            uiState = NoticiasUiState(isLoading = true),
            onCategoryChange = {},
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Nenhum Item Encontrado")
@Composable
fun NoticiasScreenEmptyPreview() {
    MeuIFPOATheme {
        NoticiasScreenContent(
            uiState = NoticiasUiState(noticias = emptyList(), editais = emptyList()),
            onCategoryChange = {},
            onSearchQueryChange = {}
        )
    }
}
