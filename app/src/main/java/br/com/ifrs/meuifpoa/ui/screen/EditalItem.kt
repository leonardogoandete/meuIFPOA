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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.model.Edital
import br.com.ifrs.meuifpoa.ui.theme.md_theme_light_surfaceVariant
import br.com.ifrs.meuifpoa.utils.Constants

@Composable
fun EditalItem(edital: Edital) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable {
                val linkValue = edital.link?.trim()
                if (!linkValue.isNullOrBlank()) {
                    val finalUrl = if (linkValue.startsWith("http://") || linkValue.startsWith("https://")) {
                        linkValue // It's already a full URL
                    } else {
                        Constants.BASE_URL_NOTICIA + linkValue // It's a relative path
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
                    context.startActivity(intent)
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = md_theme_light_surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = edital.titulo ?: stringResource(R.string.untitled_notice),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 16.sp,
                 modifier = Modifier.padding(bottom = 8.dp) // Add padding to separate from date
            )
            // Use the correct field and handle potential null value
            Text(
                text = edital.dataPublicacaoEdital ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
