package com.von.pokedex2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.von.pokedex2.R
import androidx.compose.ui.Alignment

@Composable
fun Header() {
    val context = LocalContext.current
    val imageLoader = coil.ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .offset(y = (-60).dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.pokedex2)
                .build(),
            contentDescription = "Pokedex GIF",
            imageLoader = imageLoader,
            modifier = Modifier.height(250.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.pokemon_logo),
            contentDescription = "Pokemon Logo",
            modifier = Modifier
                    .size(90.dp)
                    .offset(x = (-50).dp, y = (-50).dp)
                    .graphicsLayer(rotationZ = -20f)
        )
    }
}
