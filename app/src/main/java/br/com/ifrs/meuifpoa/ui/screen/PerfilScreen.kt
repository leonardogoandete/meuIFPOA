package br.com.ifrs.meuifpoa.ui.screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.ui.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    onLogout: () -> Unit,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val perfil by perfilViewModel.perfil.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        perfilViewModel.carregarPerfil()
    }

    if (perfil == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val base64Image = perfil?.imgPerfil
            if (!base64Image.isNullOrEmpty()) {
                val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ifrs_poa_logo),
                    contentDescription = "Logo IFRS",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileDetailRow("Nome:", perfil?.nomeDocente)
            ProfileDetailRow("Matrícula:", perfil?.matricula)
            ProfileDetailRow("Curso:", perfil?.curso)
            ProfileDetailRow("Nível:", perfil?.nivel)
            ProfileDetailRow("Situação:", perfil?.status)
            ProfileDetailRow("Ingresso:", perfil?.anoIngresso)

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                perfilViewModel.sair(context)
                onLogout()
            }) {
                Text("Sair")
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = value ?: "Não disponível", fontSize = 16.sp)
    }
}
