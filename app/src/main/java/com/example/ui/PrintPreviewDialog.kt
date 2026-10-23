package com.example.ui

import android.content.Context
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.PlatePrintEngine
import com.example.model.PlateDesignConfig

@Composable
fun PrintPreviewDialog(
    config: PlateDesignConfig,
    onDismiss: () -> Unit,
    onDirectPrint: () -> Unit,
    onSharePdf: () -> Unit,
    onShareImage: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(26.dp))
                .testTag("print_preview_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLandscape = PlatePrintEngine.isLandscapeRequired(config)
                    Column {
                        Text(
                            text = "A4 1:1 Scale Print",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isLandscape) "Standard A4 Landscape (297 × 210 mm)" else "Standard A4 Portrait (210 × 297 mm)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_preview_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scale Notice Banner
                val isLandscape = PlatePrintEngine.isLandscapeRequired(config)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isLandscape) {
                                "Wide plate formatted in Landscape A4 (297 × 210 mm) for 100% full-bleed fit without clipping."
                            } else {
                                "In printer settings, select \"100% Scale\" or \"Actual Size\" for exact BRTA physical millimeters."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Simulated A4 Paper (floating with physical drop shadow, auto-orienting to Landscape or Portrait)
                val paperAspectRatio = if (isLandscape) 297f / 210f else 210f / 297f
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (isLandscape) 0.98f else 0.88f)
                        .aspectRatio(paperAspectRatio)
                        .shadow(elevation = 14.dp, shape = RoundedCornerShape(6.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .border(0.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                        .testTag("a4_sheet_preview"),
                    contentAlignment = Alignment.Center
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Canvas(modifier = Modifier.matchParentSize()) {

                        val canvasW = size.width
                        val canvasH = size.height

                        drawContext.canvas.nativeCanvas.let { native ->
                            PlatePrintEngine.drawA4PrintPage(
                                canvas = native,
                                config = config,
                                pageWidthPt = canvasW,
                                pageHeightPt = canvasH,
                                customTypeface = PlatePrintEngine.getBanglaTypeface(context)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(22.dp))

                // Primary Direct Print Action Button
                Button(
                    onClick = onDirectPrint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("direct_print_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Print via Android Print Service",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Export Actions: Share PDF & Export High-Res Image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onSharePdf,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("share_pdf_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share PDF", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share PDF", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onShareImage,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("export_image_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Export PNG", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save PNG", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
