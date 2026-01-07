package br.com.ifrs.meuifpoa.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Initialize Google Sign-In
    LaunchedEffect(Unit) {
        loginViewModel.configureGoogleSignIn(context)
    }

    // Handle successful login navigation
    if (uiState.loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    // Show errors
    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        loginViewModel.clearError()
    }

    // Activity result launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        if (data != null) {
            loginViewModel.onGoogleSignInResult(data)
        }
    }

    // Launch Google Sign-In intent when it's ready
    uiState.googleSignInIntent?.let {
        LaunchedEffect(it) {
            googleSignInLauncher.launch(it)
            loginViewModel.resetGoogleSignInIntent() // Reset after launching
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp), // Add more horizontal padding
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ifrs_logo),
            contentDescription = "IFRS Logo",
            modifier = Modifier.fillMaxWidth(0.7f) // Adjust size
        )
        Spacer(modifier = Modifier.height(64.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { loginViewModel.startGoogleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.googleg_standard_color_18), // Assuming a google icon drawable
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Entrar com Google", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {})
}
