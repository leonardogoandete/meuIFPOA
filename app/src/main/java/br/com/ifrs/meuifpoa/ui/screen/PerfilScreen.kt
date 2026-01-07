package br.com.ifrs.meuifpoa.ui.screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.MenuBook // Explicit import
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val perfilState = perfil

    if (perfilState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the column scrollable
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val base64Image = perfilState.imgPerfil
            val imageBitmap = remember(base64Image) {
                if (!base64Image.isNullOrEmpty()) {
                    try {
                        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)?.asImageBitmap()
                    } catch (e: IllegalArgumentException) { null }
                } else { null }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ifrs_poa_logo),
                    contentDescription = "Logo IFRS",
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                ProfileInfoRow(icon = Icons.Default.Person, label = "Nome", value = perfilState.nomeDocente)
                ProfileInfoRow(icon = Icons.Default.AccountBox, label = "Matrícula", value = perfilState.matricula)
                ProfileInfoRow(icon = Icons.Default.MenuBook, label = "Curso", value = perfilState.curso)
                ProfileInfoRow(icon = Icons.Default.Star, label = "Nível", value = perfilState.nivel)
                ProfileInfoRow(icon = Icons.Default.CheckCircle, label = "Situação", value = perfilState.status)
                ProfileInfoRow(icon = Icons.Default.DateRange, label = "Ingresso", value = perfilState.anoIngresso)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                perfilViewModel.sair(context)
                onLogout()
            }) {
                Text("Sair")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = value ?: "Não disponível", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
