package br.com.ifrs.meuifpoa.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.ui.dialog.PasswordPromptDialog
import br.com.ifrs.meuifpoa.ui.viewmodel.HomeViewModel
import br.com.ifrs.meuifpoa.utils.Constants
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.carregarPerfil()
    }

    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        homeViewModel.clearError()
    }

    uiState.documentResult?.let {
        val pdfAsBytes = Base64.decode(it.pdfbase64, Base64.DEFAULT)
        val file = File(context.cacheDir, "documento.pdf")
        FileOutputStream(file).use { fos -> fos.write(pdfAsBytes) }
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
        homeViewModel.clearDocumentResult()
    }

    if (uiState.showPasswordDialog) {
        PasswordPromptDialog(
            onDismiss = { homeViewModel.onDialogDismiss() },
            onConfirm = { homeViewModel.onPasswordConfirm(it, context) }
        )
    }

    if (uiState.isProfileLoading && uiState.perfil == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        uiState.perfil?.let { perfil ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Olá, ${perfil.nomeDocente?.substringBefore(" ") ?: "Aluno"}!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                val progress = perfil.integralizado?.toIntOrNull() ?: 0
                IntegralizacaoChart(progress = progress)

                Spacer(modifier = Modifier.height(16.dp))

                // Course Hours Section - RE-ADDED
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    HourInfo(label = "CH Obrigatória Pendente", value = perfil.chObrigatoriaPendente)
                    HourInfo(label = "CH Optativa Pendente", value = perfil.chOptativaPendente)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    HourInfo(label = "CH Complementar Pendente", value = perfil.chComplementarPendente)
                    HourInfo(label = "CH Total Curriculo", value = perfil.chTotalCurriculo)
                }

                Spacer(modifier = Modifier.height(24.dp))

                val documentTypes = listOf(
                    "Histórico" to Constants.DOC_HISTORICO,
                    "Histórico com Ementas" to Constants.DOC_HISTORICO_EMENTAS,
                    "Declaração de Vínculo" to Constants.DOC_DECLARACAO_VINCULO,
                    "Atestado de Matrícula" to Constants.DOC_ATESTADO_MATRICULA
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(documentTypes) { (title, type) ->
                        DocumentButton(
                            text = title,
                            isLoading = uiState.loadingDocumentType == type,
                            isAnyLoading = uiState.isDocumentLoading,
                            onClick = { homeViewModel.onEmitirDocumentoClick(type) }
                        )
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Perfil não encontrado. Tente novamente mais tarde.")
        }
    }
}

// Re-added HourInfo Composable
@Composable
private fun HourInfo(label: String, value: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value ?: "--", 
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentButton(
    text: String,
    isLoading: Boolean,
    isAnyLoading: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth(),
        enabled = !isAnyLoading,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color.White)
            } else {
                Text(
                    text = text, 
                    textAlign = TextAlign.Center, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
