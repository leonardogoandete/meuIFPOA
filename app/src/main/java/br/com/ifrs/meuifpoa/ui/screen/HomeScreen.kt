package br.com.ifrs.meuifpoa.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.MeuIFPOAApplication
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.dialog.PasswordPromptDialog
import br.com.ifrs.meuifpoa.ui.viewmodel.HomeViewModel
import br.com.ifrs.meuifpoa.utils.Constants
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen() {
    val appContainer = (LocalContext.current.applicationContext as MeuIFPOAApplication).container
    val homeViewModel: HomeViewModel = viewModel(factory = appContainer.viewModelFactory)

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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(
                        R.string.welcome_message_user,
                        perfil.nomeDocente?.substringBefore(" ") ?: stringResource(R.string.student_placeholder)
                    ),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                val progress = perfil.integralizado?.toIntOrNull() ?: 0
                IntegralizacaoChart(progress = progress)

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.workload_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    HourInfo(label = stringResource(R.string.workload_mandatory_label), value = perfil.chObrigatoriaPendente, modifier = Modifier.weight(1f))
                    HourInfo(label = stringResource(R.string.workload_elective_label), value = perfil.chOptativaPendente, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    HourInfo(label = stringResource(R.string.workload_complementary_label), value = perfil.chComplementarPendente, modifier = Modifier.weight(1f))
                    HourInfo(label = stringResource(R.string.workload_total_label), value = perfil.chTotalCurriculo, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        DocumentButton(
                            text = stringResource(R.string.transcript_button),
                            isLoading = uiState.loadingDocumentType == Constants.DOC_HISTORICO,
                            isAnyLoading = uiState.isDocumentLoading,
                            onClick = { homeViewModel.onEmitirDocumentoClick(Constants.DOC_HISTORICO) }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        DocumentButton(
                            text = stringResource(R.string.transcript_with_syllabi_button),
                            isLoading = uiState.loadingDocumentType == Constants.DOC_HISTORICO_EMENTAS,
                            isAnyLoading = uiState.isDocumentLoading,
                            onClick = { homeViewModel.onEmitirDocumentoClick(Constants.DOC_HISTORICO_EMENTAS) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        DocumentButton(
                            text = stringResource(R.string.enrollment_statement_button),
                            isLoading = uiState.loadingDocumentType == Constants.DOC_DECLARACAO_VINCULO,
                            isAnyLoading = uiState.isDocumentLoading,
                            onClick = { homeViewModel.onEmitirDocumentoClick(Constants.DOC_DECLARACAO_VINCULO) }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        DocumentButton(
                            text = stringResource(R.string.proof_of_enrollment_button),
                            isLoading = uiState.loadingDocumentType == Constants.DOC_ATESTADO_MATRICULA,
                            isAnyLoading = uiState.isDocumentLoading,
                            onClick = { homeViewModel.onEmitirDocumentoClick(Constants.DOC_ATESTADO_MATRICULA) }
                        )
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.profile_not_found_error))
        }
    }
}

@Composable
private fun HourInfo(label: String, value: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth(),
        enabled = !isAnyLoading, // Disable the card if any document is loading
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Explicitly set disabled color
            disabledContentColor = Color.White.copy(alpha = 0.8f) // Explicitly set disabled content color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                // This specific button is loading, show a spinner
                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = Color.White)
            } else {
                // This button is not the one loading, show the text.
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
