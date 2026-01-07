package br.com.ifrs.meuifpoa.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.ifrs.meuifpoa.ui.Screen
import br.com.ifrs.meuifpoa.ui.screen.LoginScreen
import br.com.ifrs.meuifpoa.ui.screen.MainScreen
import br.com.ifrs.meuifpoa.ui.theme.MeuIFPOATheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        setContent {
            MeuIFPOATheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (auth.currentUser != null) Screen.Main.route else Screen.Login.route
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        })
                    }
                    composable(Screen.Main.route) {
                        MainScreen(onLogout = {
                            auth.signOut()
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
