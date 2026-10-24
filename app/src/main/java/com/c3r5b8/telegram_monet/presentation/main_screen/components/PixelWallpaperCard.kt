package com.c3r5b8.telegram_monet.presentation.main_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.c3r5b8.telegram_monet.R
import com.c3r5b8.telegram_monet.wallpaper.PixelWallpaperGenerator
import com.c3r5b8.telegram_monet.wallpaper.WallpaperConfig
import com.c3r5b8.telegram_monet.wallpaper.WallpaperPreset

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PixelWallpaperCard(
    colors: Map<String, Int>,
    preset: WallpaperPreset,
    seed: Long,
    isDarkPreview: Boolean,
    isAmoled: Boolean,
    onSelectPreset: (WallpaperPreset) -> Unit,
    onShuffle: () -> Unit,
    onToggleDarkPreview: (Boolean) -> Unit,
    onSaveWallpaper: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val wallpaperConfig = remember(preset, seed, isDarkPreview, isAmoled) {
        WallpaperConfig(
            preset = preset,
            seed = seed,
            isDark = isDarkPreview,
            isAmoled = isAmoled,
        )
    }

    BasicCard(
        title = R.string.wallpaper_card_title,
        icon = R.drawable.palette_icon,
        description = R.string.wallpaper_card_description,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = stringResource(R.string.wallpaper_card_description),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Phone Frame Preview
            ElevatedCard(
                modifier = Modifier
                    .width(115.dp)
                    .aspectRatio(9f / 19.5f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    PixelWallpaperGenerator.renderToDrawScope(
                        drawScope = this,
                        colors = colors,
                        config = wallpaperConfig,
                    )
                }
            }

            // Controls Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Preset Selection Chips
                Text(
                    text = stringResource(R.string.scheme_selector_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    WallpaperPreset.entries.forEach { p ->
                        FilterChip(
                            selected = p == preset,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSelectPreset(p)
                            },
                            label = { Text(stringResource(p.titleRes), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Shuffle button
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onShuffle()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("🎲 " + stringResource(R.string.wallpaper_shuffle))
                }

                // Dark Preview Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleDarkPreview(!isDarkPreview) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.wallpaper_preview_dark),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Switch(
                        checked = isDarkPreview,
                        onCheckedChange = onToggleDarkPreview,
                        colors = SwitchDefaults.colors(),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save Wallpaper button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSaveWallpaper()
                },
            shape = ButtonDefaults.shape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Box(
                modifier = Modifier.padding(ButtonDefaults.ContentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.wallpaper_save),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
