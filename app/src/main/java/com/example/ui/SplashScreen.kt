package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

/**
 * Opening / Splash Screen for PlateCraft BD.
 * Seamlessly integrates the user's custom vehicle plate studio emblem
 * into an ultra-polished automotive deep emerald & gold studio stage.
 */
@Composable
fun SplashScreen(
    onTimeoutOrSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Entrance animations
    val logoScale = remember { Animatable(0.78f) }
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    var progressVal by remember { mutableFloatStateOf(0.08f) }
    var currentStatus by remember { mutableStateOf("স্টুডিও লোড হচ্ছে…") }

    // Ambient pulsing glow behind logo
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow_pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    LaunchedEffect(Unit) {
        // Step 1: Animate logo into view with gentle spring
        logoAlpha.animateTo(1f, animationSpec = tween(400))
        logoScale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Step 2: Animate text & content
        contentAlpha.animateTo(1f, animationSpec = tween(350))

        // Progressive loading steps
        delay(250)
        progressVal = 0.45f
        currentStatus = "BRTA ফন্ট ও জেলা সেটআপ হচ্ছে…"

        delay(450)
        progressVal = 0.85f
        currentStatus = "ডিজিটাল প্লেট ইঞ্জিন প্রস্তুত…"

        delay(400)
        progressVal = 1.0f
        currentStatus = "স্টুডিও প্রস্তুত!"

        delay(300)
        onTimeoutOrSkip()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF021B14),
                        Color(0xFF042B23),
                        Color(0xFF084B43),
                        Color(0xFF05382B),
                        Color(0xFF021610)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTimeoutOrSkip
            )
    ) {
        // Decorative background automotive radial spotlights
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(glowScale)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E676).copy(alpha = 0.18f),
                            Color(0xFFC8A96A).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Top subtle badge header
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF021812).copy(alpha = 0.65f))
                .border(1.dp, Color(0xFFC8A96A).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = Color(0xFFC8A96A),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "গণপ্রজাতন্ত্রী বাংলাদেশ অনুমোদিত ফরম্যাট",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE8F3EF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Center Content: Branded Logo Emblem + Typography
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo: Pure logo emblem with zero black borders
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground_asset),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .size(136.dp)
                    .scale(logoScale.value)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = Color(0xFF00E676)
                    )
                    .clip(RoundedCornerShape(30.dp))
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App Title & Studio Branding
            Text(
                text = stringResource(id = R.string.splash_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp,
                modifier = Modifier.scale(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(id = R.string.splash_subtitle),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA7F3D0),
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // BRTA Standard & Dynamic App Version Badges
            Row(
                modifier = Modifier.scale(contentAlpha.value),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFC8A96A).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8A96A).copy(alpha = 0.45f))
                ) {
                    Text(
                        text = stringResource(id = R.string.splash_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFE082),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "v${com.example.BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE8F3EF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Bottom Progress (Button 2 removed)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp, start = 36.dp, end = 36.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant linear progress indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressVal)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00E676),
                                    Color(0xFFFFD54F)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current loading status label
            Text(
                text = currentStatus,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE8F3EF).copy(alpha = 0.85f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Version Label
            Text(
                text = "সংস্করণ ${com.example.BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp
            )
        }
    }
}
