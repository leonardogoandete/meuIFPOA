package br.com.ifrs.meuifpoa.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ifrs.meuifpoa.model.Noticia
import br.com.ifrs.meuifpoa.ui.theme.md_theme_light_primaryContainer

@Composable
fun NoticiaItem(noticia: Noticia) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                noticia.link?.let {
                    val url = br.com.ifrs.meuifpoa.utils.Constants.BASE_URL_NOTICIA + it
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = md_theme_light_primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = noticia.titulo ?: "Sem título",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 16.sp
            )
            Text(
                text = noticia.resumo ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = noticia.dataHoraPublicacao,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            )
        }
    }
}
