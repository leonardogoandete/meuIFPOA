package br.com.ifrs.meuifpoa.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegisterSuccess: () -> Unit,
    registroViewModel: RegistroViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") } // Corrected typo
    val isLoading by registroViewModel.isLoading.collectAsState()
    val error by registroViewModel.registroError.collectAsState()
    val registerSuccess by registroViewModel.registroSuccess.collectAsState()
    val context = LocalContext.current

    if (registerSuccess) {
        LaunchedEffect(Unit) {
            onRegisterSuccess()
        }
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        registroViewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.titulo_cadastro),
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text(stringResource(R.string.txtViewNomeCadastro)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = cpf,
            onValueChange = { cpf = it },
            label = { Text(stringResource(R.string.txtViewCpfCadastro)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.txtViewEmailCadastro)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text(stringResource(R.string.txtViewSenhaCadastro)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { registroViewModel.registrar(nome, cpf, email, senha) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(stringResource(R.string.btnRegistrarCadastro), color = Color.White)
            }
        }
    }
}
