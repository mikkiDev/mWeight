package com.example.mweight

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mweight.ui.theme.MWeightTheme

@Composable
fun HomeScreen(onNavigateToApp: () -> Unit) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onNavigateToApp() }
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 10.dp).padding(vertical = 50.dp),
            horizontalArrangement = Arrangement.End
        ) {
            val version = "1.0"
            Text(
                text = "version $version",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Row(
            modifier                = Modifier.padding(all = 8.dp),
            horizontalArrangement   = Arrangement.Center,
            verticalAlignment       = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.wooper2),
                contentDescription = "upa!",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {

                Surface(
                ) {
                    Text(
                        text = "mikkiDev",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }


                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "mWeight",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }

        val infiniteTransition = rememberInfiniteTransition()
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1000
                    0.65f at 500
                },
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Row(
            modifier = Modifier.padding(all = 100.dp),
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "tap anywhere to continue",
                modifier = Modifier.alpha(alpha)
            )
        }

    }

}