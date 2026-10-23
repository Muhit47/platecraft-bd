package com.example.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.PlatePrintEngine
import com.example.model.BanglaConstants
import com.example.model.BorderStyle
import com.example.model.PlateDesignConfig
import com.example.model.PlateTheme
import com.example.model.PlateType
import kotlinx.coroutines.launch

@Composable
fun CardSectionHead(
    number: String,
    title: String,
    subtitle: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = com.example.ui.theme.PlateCraftGreenSoft,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = com.example.ui.theme.PlateCraftGreen,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = number,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.PlateCraftGreenDark
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = com.example.ui.theme.PlateCraftMuted,
                    modifier = Modifier.padding(start = 34.dp, top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlateEditorScreen(
    viewModel: PlateEditorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val config by viewModel.config.collectAsStateWithLifecycle()
    val savedPlates by viewModel.savedPlates.collectAsStateWithLifecycle()
    val showPrintPreview by viewModel.showPrintPreview.collectAsStateWithLifecycle()
    val showSavedPlates by viewModel.showSavedPlates.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()



    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = com.example.ui.theme.PlateCraftGreenDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0B5A46),
                                        Color(0xFF084B43),
                                        Color(0xFF063A36)
                                    )
                                )
                            )
                    ) {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Official App Logo Badge
                                    Image(
                                        painter = painterResource(id = com.example.R.drawable.ic_launcher_foreground_asset),
                                        contentDescription = "App Logo",
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "বাংলাদেশ নাম্বার প্লেট মেকার",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "PlateCraft BD v${com.example.BuildConfig.VERSION_NAME} • BRTA Standard",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Print Preview Icon
                                IconButton(
                                    onClick = { viewModel.openPrintPreview() },
                                    modifier = Modifier.testTag("open_print_preview_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = "Print & Export",
                                        tint = Color.White
                                    )
                                }
                                
                                // Saved Library Icon
                                IconButton(
                                    onClick = { viewModel.openSavedPlates() },
                                    modifier = Modifier.testTag("open_saved_plates_button")
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (savedPlates.isNotEmpty()) {
                                                Badge(containerColor = com.example.ui.theme.PlateCraftRed) {
                                                    Text("${savedPlates.size}")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (savedPlates.isNotEmpty()) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Saved Plates",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White
                            )
                        )
                    }
                }
                // Signature bottom accent strip: 3dp gradient red & gold
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    com.example.ui.theme.PlateCraftRed,
                                    com.example.ui.theme.PlateCraftRed,
                                    com.example.ui.theme.PlateCraftGold
                                )
                            )
                        )
                )
            }
        },
        bottomBar = {
            // Sleek Floating Dock Bottom Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveCurrentPlate() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("bottom_save_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            com.example.ui.theme.PlateCraftGreen
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.ui.theme.PlateCraftGreenDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.openPrintPreview() },
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("bottom_print_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.ui.theme.PlateCraftGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1:1 A4 Print Preview", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Vehicle Type Segmented Pill Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlateType.values().forEach { type ->
                    val isSelected = config.plateType == type
                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        animationSpec = tween(200),
                        label = "pill_color"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(200),
                        label = "content_color"
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = containerColor,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ) else null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.setPlateType(type) }
                            .testTag("vehicle_pill_${type.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (type) {
                                PlateType.BIKE_FRONT -> Icons.Default.DirectionsBike
                                PlateType.BIKE_REAR -> Icons.Default.TwoWheeler
                                PlateType.SCOOTER -> Icons.Default.TwoWheeler
                                PlateType.PRIVATE_CAR -> Icons.Default.DirectionsCar
                                PlateType.CUSTOM -> Icons.Default.Settings
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = contentColor
                            )
                        }
                    }
                }
            }

            // 2. The Hero Component: Studio Showcase Stage
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                PlateCanvasCard(
                    config = config,
                    onQuickThemeChange = { viewModel.setTheme(it) }
                )
            }

            // Custom Dimensions Slider (if custom type is active)
            AnimatedVisibility(
                visible = config.plateType == PlateType.CUSTOM,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Custom Dimensions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Width: ${config.customWidthMm.toInt()} mm", style = MaterialTheme.typography.labelMedium)
                                Slider(
                                    value = config.customWidthMm,
                                    onValueChange = { viewModel.setCustomDimensions(it, config.customHeightMm) },
                                    valueRange = 100f..400f
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Height: ${config.customHeightMm.toInt()} mm", style = MaterialTheme.typography.labelMedium)
                                Slider(
                                    value = config.customHeightMm,
                                    onValueChange = { viewModel.setCustomDimensions(config.customWidthMm, it) },
                                    valueRange = 40f..250f
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Registration Studio Card (Minimalist Twin Number Displays & District)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("registration_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PlateCraftLine),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                CardSectionHead(
                    number = "১",
                    title = "রেজিস্ট্রেশন নম্বর / Registration Details",
                    subtitle = "মেট্রো অঞ্চল, যানবাহনের শ্রেণি ও নম্বর লিখুন"
                )
                Column(modifier = Modifier.padding(18.dp)) {

                    // 1. District / Metro Dropdown Menu
                    var showDistrictDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (showDistrictDropdown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDistrictDropdown = true }
                                .testTag("district_dropdown_trigger")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "DISTRICT / METRO (মেট্রো / জেলা)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = config.metroName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select District",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showDistrictDropdown,
                            onDismissRequest = { showDistrictDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .heightIn(max = 380.dp)
                                .testTag("district_dropdown_menu")
                        ) {
                            BanglaConstants.METRO_LIST.forEach { metro ->
                                val isSelected = config.metroName == metro
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = metro,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.setMetro(metro)
                                        showDistrictDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Vehicle Class Dropdown Menu (with detailed category description)
                    val currentClassInfo = BanglaConstants.VEHICLE_CLASSES.find { it.letter == config.classLetter }
                    var showClassDropdown by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (showClassDropdown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClassDropdown = true }
                                .testTag("class_dropdown_trigger")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = config.classLetter,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "VEHICLE CLASS (যানবাহনের শ্রেণি)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${config.classLetter} (${currentClassInfo?.english ?: ""})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentClassInfo?.category ?: "Class specifications",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Class",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showClassDropdown,
                            onDismissRequest = { showClassDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .heightIn(max = 400.dp)
                                .testTag("class_dropdown_menu")
                        ) {
                            BanglaConstants.VEHICLE_CLASSES.forEach { item ->
                                val isSelected = config.classLetter == item.letter
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = item.letter,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${item.letter} — ${item.english}",
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = item.category,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.setClassLetter(item.letter)
                                        showClassDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Twin Number Cards (Series & Serial with prominent bold Bengali numbers)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Series Card
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SERIES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = config.seriesNumber,
                                onValueChange = { viewModel.setSeriesNumber(it) },
                                singleLine = true,
                                placeholder = { Text("১১") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                supportingText = { Text("2 digits (EN/BN)") },
                                modifier = Modifier.fillMaxWidth().testTag("series_input_field")
                            )
                        }

                        // Serial Card
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = "SERIAL NUMBER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = config.serialNumber,
                                onValueChange = { viewModel.setSerialNumber(it) },
                                singleLine = true,
                                placeholder = { Text("১২৩৪") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                ),
                                supportingText = { Text("4 digits (EN/BN)") },
                                modifier = Modifier.fillMaxWidth().testTag("serial_input_field")
                            )
                        }
                    }
                }
            }

            // 4. Badges & Accessories Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("badges_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PlateCraftLine),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                CardSectionHead(
                    number = "২",
                    title = "নিরাপত্তা ও ব্যক্তিগত ব্যাজ / Safety Badges",
                    subtitle = "জরুরী ব্লাড গ্রুপ, জাতীয় পতাকা ও হলোগ্রাম"
                )
                Column(modifier = Modifier.padding(18.dp)) {

                    // Blood Group Pills
                    Text(
                        text = "EMERGENCY BLOOD GROUP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BanglaConstants.BLOOD_GROUPS.forEach { bg ->
                            val isSelected = config.bloodGroup == bg
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setBloodGroup(bg) },
                                label = { Text(bg) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Rider Tag / Club Motto Field
                    OutlinedTextField(
                        value = config.customTagline,
                        onValueChange = { viewModel.setCustomTagline(it) },
                        label = { Text("Custom Rider Tag / Motto (Optional)") },
                        placeholder = { Text("e.g. RIDE SAFE / BIKERS OF BD") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("tagline_input_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Emblems & Security Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("National Flag Emblem", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Green and red Bangladesh circle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = config.showEmblem,
                            onCheckedChange = { viewModel.toggleEmblem(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("toggle_emblem_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("BRTA Hologram Marker", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Official digital RFID marker", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = config.showHologram,
                            onCheckedChange = { viewModel.toggleHologram(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("toggle_hologram_switch")
                        )
                    }
                }
            }

            // 5. Frame & Mount Options Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("mount_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PlateCraftLine),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                CardSectionHead(
                    number = "৩",
                    title = "ফ্রেম ও মাউন্টিং গাইড / Frame & Mounting",
                    subtitle = "বর্ডার স্টাইল, স্ক্রু ড্রিল ও A4 কাটিং গাইড"
                )
                Column(modifier = Modifier.padding(18.dp)) {

                    // Border style selector chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BorderStyle.values().forEach { border ->
                            val isSelected = config.borderStyle == border
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setBorderStyle(border) },
                                label = { Text(border.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Drill Bolt Holes", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("5mm mounting points for bike/car brackets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = config.showScrewHoles,
                            onCheckedChange = { viewModel.toggleScrewHoles(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("toggle_holes_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("A4 Print Cut Lines", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Crop marks & cutting outline for precision", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = config.showCutGuides,
                            onCheckedChange = { viewModel.toggleCutGuides(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("toggle_cut_guides_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Print Preview Modal Dialog
    if (showPrintPreview) {
        PrintPreviewDialog(
            config = config,
            onDismiss = { viewModel.closePrintPreview() },
            onDirectPrint = {
                activity?.let {
                    PlatePrintEngine.printPlateDirectly(it, config)
                } ?: Toast.makeText(context, "Printer service ready", Toast.LENGTH_SHORT).show()
            },
            onSharePdf = {
                scope.launch {
                    try {
                        val pdf = PlatePrintEngine.createA4PdfFile(context, config)
                        val intent = PlatePrintEngine.createSharePdfIntent(context, pdf)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error sharing PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onShareImage = {
                scope.launch {
                    try {
                        val bitmap = PlatePrintEngine.createHighResBitmap(config, context)
                        val imgFile = PlatePrintEngine.saveBitmapToFile(context, bitmap, config)
                        val intent = PlatePrintEngine.createShareImageIntent(context, imgFile)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error saving PNG: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        )
    }

    // Saved Plates Modal Dialog
    if (showSavedPlates) {
        SavedPlatesDialog(
            plates = savedPlates,
            onDismiss = { viewModel.closeSavedPlates() },
            onSelectPlate = { viewModel.loadPlate(it) },
            onDeletePlate = { viewModel.deletePlate(it) }
        )
    }
}
