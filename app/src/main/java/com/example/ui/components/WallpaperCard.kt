package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WallpaperItem

@Composable
fun WallpaperCard(
    wallpaper: WallpaperItem,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSelect)
            .testTag("wallpaper_card_${wallpaper.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F141F)
        ),
        border = BorderStroke(1.dp, Color(wallpaper.accentColorHex).copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
            ) {
                Image(
                    painter = painterResource(id = wallpaper.resId),
                    contentDescription = wallpaper.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xCC07090E), Color.Transparent)
                            )
                        )
                )

                // Bottom gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xF007090E))
                            )
                        )
                )

                // Series badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xCC07090E),
                    border = BorderStroke(1.dp, Color(wallpaper.accentColorHex)),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = wallpaper.seriesBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(wallpaper.accentColorHex),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite icon button
                Surface(
                    shape = CircleShape,
                    color = Color(0xCC07090E),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(38.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("btn_fav_${wallpaper.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite wallpaper",
                            tint = if (isFavorite) Color(0xFFFF2A6D) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 3D Parallax indicator chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(wallpaper.accentColorHex).copy(alpha = 0.25f),
                    border = BorderStroke(0.8.dp, Color(wallpaper.accentColorHex)),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(wallpaper.accentColorHex), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D Gyro POP",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Fullscreen preview action pill
                Surface(
                    shape = CircleShape,
                    color = Color(0xDD07090E),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    IconButton(
                        onClick = onPreview,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_preview_${wallpaper.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Preview in Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Card Bottom Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = wallpaper.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFF1F5F9),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallpaper.resolution,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${wallpaper.fps} FPS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(wallpaper.accentColorHex),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
