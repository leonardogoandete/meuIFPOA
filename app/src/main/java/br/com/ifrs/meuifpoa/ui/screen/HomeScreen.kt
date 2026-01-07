package br.com.ifrs.meuifpoa.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.ui.dialog.ForgotPasswordDialog
import br.com.ifrs.meuifpoa.ui.viewmodel.HomeViewModel
import br.com.ifrs.meuifpoa.utils.Constants
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Trigger data loading when the screen is first composed
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
        ForgotPasswordDialog(
            onDismiss = { homeViewModel.onDialogDismiss() },
            onConfirm = { homeViewModel.onPasswordConfirm(it, context) }
        )
    }

    if (uiState.isLoading) {
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
                Text("Olá, ${perfil.nomeDocente}!")
                Spacer(modifier = Modifier.height(16.dp))

                // TODO: Adicionar gráfico de pizza aqui

                Spacer(modifier = Modifier.height(32.dp))
                DocumentButton(text = "Emitir Histórico", documentType = Constants.DOC_HISTORICO, viewModel = homeViewModel)
                DocumentButton(text = "Emitir Histórico com Ementas", documentType = Constants.DOC_HISTORICO_EMENTAS, viewModel = homeViewModel)
                DocumentButton(text = "Emitir Declaração de Vínculo", documentType = Constants.DOC_DECLARACAO_VINCULO, viewModel = homeViewModel)
                DocumentButton(text = "Emitir Atestado de Matrícula", documentType = Constants.DOC_ATESTADO_MATRICULA, viewModel = homeViewModel)

            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Perfil não encontrado")
        }
    }
}

@Composable
fun DocumentButton(text: String, documentType: String, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Button(
        onClick = { viewModel.onEmitirDocumentoClick(documentType) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = uiState.documentLoadingState[documentType] != true
    ) {
        if (uiState.documentLoadingState[documentType] == true) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(text)
        }
    }
}
