package com.c3r5b8.telegram_monet.wallpaper

import androidx.annotation.StringRes
import com.c3r5b8.telegram_monet.R

enum class WallpaperPreset(@param:StringRes val titleRes: Int) {
    M3_EXPRESSIVE(R.string.wallpaper_preset_m3_expressive),
    PIXEL_MINIMAL(R.string.wallpaper_preset_pixel_minimal),
    PIXEL_BLOOM(R.string.wallpaper_preset_pixel_bloom),
    SILK_DUNES(R.string.wallpaper_preset_silk_dunes),
    PIXEL_CLASSIC(R.string.wallpaper_preset_pixel_classic),
    FLUID_WAVES(R.string.wallpaper_preset_fluid_waves),
    BAUHAUS_MODERN(R.string.wallpaper_preset_bauhaus);
}

data class WallpaperConfig(
    val preset: WallpaperPreset = WallpaperPreset.M3_EXPRESSIVE,
    val seed: Long = 42L,
    val isDark: Boolean = false,
    val isAmoled: Boolean = false,
)
