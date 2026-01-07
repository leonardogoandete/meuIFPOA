package br.com.ifrs.meuifpoa.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity // Changed from AppCompatActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.ifrs.meuifpoa.ui.Screen
import br.com.ifrs.meuifpoa.ui.screen.*
import br.com.ifrs.meuifpoa.ui.theme.MeuIFPOATheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() { // Changed from AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        setContent {
            MeuIFPOATheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (auth.currentUser != null) Screen.Main.route else Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen { // Updated to pass lambda
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onRegisterClick = { navController.navigate(Screen.Register.route) },
                            onForgotPasswordClick = { /* TODO */ }
                        )
                    }
                    composable(Screen.Register.route) {
                        RegistroScreen(
                            onRegisterSuccess = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Main.route) {
                        MainScreen(onLogout = {
                            // A lógica de logout agora está no PerfilViewModel
                            // A navegação de volta para o login é tratada aqui
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}
