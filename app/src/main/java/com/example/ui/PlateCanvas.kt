package com.example.ui

import android.graphics.RectF
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PlatePrintEngine
import com.example.model.PlateDesignConfig
import com.example.model.PlateTheme

@Composable
fun PlateCanvasCard(
    config: PlateDesignConfig,
    onQuickThemeChange: (PlateTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val targetAspectRatio = config.aspectRatio
    val animatedRatio by animateFloatAsState(
        targetValue = targetAspectRatio,
        animationSpec = tween(durationMillis = 350),
        label = "aspect_ratio_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("plate_preview_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Minimal Blueprint Caliper Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Architectural Dimension Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${config.effectiveWidthMm.toInt()} × ${config.effectiveHeightMm.toInt()} mm",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 1:1 Scale Verification Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("scale_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1:1 Physical Scale",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Studio Workbench Stage: Clean aesthetic backdrop that makes the plate the pure hero
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                com.example.ui.theme.SelldiboStageStart,
                                com.example.ui.theme.SelldiboStageEnd
                            )
                        )
                    )
                    .border(
                        1.dp,
                        com.example.ui.theme.SelldiboLine,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // The floating vehicle plate
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .aspectRatio(animatedRatio)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(10.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .testTag("plate_interactive_canvas")
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .testTag("plate_native_canvas")
                    ) {

                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val plateRect = RectF(0f, 0f, canvasWidth, canvasHeight)

                        drawContext.canvas.nativeCanvas.let { native ->
                            PlatePrintEngine.drawPlateToCanvas(
                                canvas = native,
                                rect = plateRect,
                                config = config,
                                isPrintMode = false,
                                customTypeface = PlatePrintEngine.getBanglaTypeface(context)
                            )
                        }
                    }


                    // Subtle realistic metallic specular gradient overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.09f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Floating Quick Bar: Rapid Theme Swatches & Plate Format Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fast Theme Swatch Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlateTheme.values().forEach { theme ->
                        val isSelected = config.theme == theme
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 24.dp else 20.dp)
                                .clip(CircleShape)
                                .background(theme.bgColor)
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    CircleShape
                                )
                                .clickable { onQuickThemeChange(theme) }
                                .testTag("quick_theme_${theme.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                val checkTint = if (theme == PlateTheme.STANDARD_WHITE || theme == PlateTheme.COMMERCIAL_YELLOW) Color.Black else Color.White
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = checkTint,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Clean Subtitle
                Text(
                    text = "${config.plateType.bengaliTitle} • ${config.fullPlateNumberFormatted}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
