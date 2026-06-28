package com.example.mdxabsensi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mdxabsensi.R

@Composable
fun AndriCorpLogo(
    modifier: Modifier = Modifier,
    logoSize: Int = 180
) {
    Image(
        painter = painterResource(id = R.drawable.logoapp),
        contentDescription = "Logo Aplikasi",
        modifier = modifier.size(logoSize.dp),
        contentScale = ContentScale.Fit
    )
}
