package br.com.ifrs.meuifpoa.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by loginViewModel.isLoading.collectAsState()
    val error by loginViewModel.loginError.collectAsState()
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()
    val context = LocalContext.current

    if (loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        loginViewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ifrs_logo),
            contentDescription = "IFRS Logo",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.txtViewEmail)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.txtViewSenha)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { loginViewModel.realizarLogin(email, password, context) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(stringResource(R.string.btnEntrarText), color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.btnCadastrarText),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClick() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.msg_titulo_esqueci_senha),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onForgotPasswordClick() }
        )
    }
}
