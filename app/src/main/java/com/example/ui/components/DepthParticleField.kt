package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.model.ParticleType
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val depthFactor: Float,
    val color: Color
)

@Composable
fun DepthParticleField(
    particleType: ParticleType,
    accentColor: Color,
    roll: Float,
    pitch: Float,
    modifier: Modifier = Modifier
) {
    val particles = remember(particleType, accentColor) {
        val rand = Random(42)
        List(40) {
            val baseColor = when (particleType) {
                ParticleType.EMBER -> if (rand.nextBoolean()) Color(0xFFFF9E00) else Color(0xFFFF2A6D)
                ParticleType.NEON_SPARKS -> if (rand.nextBoolean()) Color(0xFF00F0FF) else Color(0xFFBD00FF)
                ParticleType.COSMIC_RUNES -> if (rand.nextBoolean()) Color(0xFFFFD166) else Color(0xFF67E8F9)
                ParticleType.VOID_MIST -> if (rand.nextBoolean()) Color(0xFFBD00FF) else Color(0xFF818CF8)
            }
            Particle(
                initialX = rand.nextFloat(),
                initialY = rand.nextFloat(),
                speed = 0.2f + rand.nextFloat() * 0.8f,
                size = 3f + rand.nextFloat() * 9f,
                alpha = 0.35f + rand.nextFloat() * 0.65f,
                depthFactor = 1.2f + rand.nextFloat() * 2.5f,
                color = baseColor
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "particle_motion")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val progress = (p.initialY - (time * p.speed)) % 1f
            val normY = if (progress < 0f) progress + 1f else progress
            val normX = (p.initialX + (roll * 0.08f * p.depthFactor)) % 1f
            val finalX = if (normX < 0f) normX + 1f else normX

            val px = finalX * w
            val py = normY * h - (pitch * 30f * p.depthFactor)

            // Dynamic 3D depth pop-out scaling
            val scale = (1f + (p.depthFactor * 0.35f))
            drawCircle(
                color = p.color.copy(alpha = (p.alpha * 0.85f).coerceIn(0f, 1f)),
                radius = p.size * scale,
                center = Offset(px, py)
            )
        }
    }
}
