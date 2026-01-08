package br.com.ifrs.meuifpoa.ui.screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                    contentDescription = stringResource(R.string.profile_picture_description),
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ifrs_poa_logo),
                    contentDescription = stringResource(R.string.ifrs_logo_description),
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_pessoa), label = stringResource(R.string.txtViewTituloNome), value = perfilState.nomeDocente)
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_matricula), label = stringResource(R.string.txtViewTituloMatricula), value = perfilState.matricula)
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_curso), label = stringResource(R.string.label_course), value = perfilState.curso)
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_nivel), label = stringResource(R.string.txtViewTituloNivel), value = perfilState.nivel)
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_status), label = stringResource(R.string.txtViewTituloSituacao), value = perfilState.status)
                ProfileInfoRow(icon = painterResource(id = R.drawable.ic_ingresso), label = stringResource(R.string.txtViewTituloIngresso), value = perfilState.anoIngresso)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                perfilViewModel.sair(context)
                onLogout()
            }) {
                Text(stringResource(id = R.string.logout_button))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: Painter, label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = value ?: stringResource(R.string.dado_nao_disponivel), style = MaterialTheme.typography.bodyLarge)
        }
    }
}