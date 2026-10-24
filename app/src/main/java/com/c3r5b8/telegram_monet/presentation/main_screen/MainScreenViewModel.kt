package com.c3r5b8.telegram_monet.presentation.main_screen

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3r5b8.telegram_monet.adapters.buildThemeBytes
import com.c3r5b8.telegram_monet.adapters.createTheme
import com.c3r5b8.telegram_monet.adapters.generateMonetColors
import com.c3r5b8.telegram_monet.adapters.getSystemMonetColors
import com.c3r5b8.telegram_monet.adapters.saveThemeBytesToDownloads
import com.c3r5b8.telegram_monet.common.ColorScheme
import com.c3r5b8.telegram_monet.common.Constants
import com.c3r5b8.telegram_monet.data.local.entity.PaletteEntity
import com.c3r5b8.telegram_monet.data.repository.PaletteRepository
import com.c3r5b8.telegram_monet.wallpaper.PixelWallpaperGenerator
import com.c3r5b8.telegram_monet.wallpaper.WallpaperConfig
import com.c3r5b8.telegram_monet.wallpaper.WallpaperPreset
import com.c3r5b8.telegram_monet.wallpaper.WallpaperStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
	contextParam: Context,
	private val paletteRepository: PaletteRepository,
) : ViewModel() {

	private val _isAmoled = MutableStateFlow(false)
	val isAmoled: StateFlow<Boolean> = _isAmoled.asStateFlow()

	private val _isFrostedBubbles = MutableStateFlow(true)
	val isFrostedBubbles: StateFlow<Boolean> = _isFrostedBubbles.asStateFlow()

	private val _isGradient = MutableStateFlow(false)
	val isGradient: StateFlow<Boolean> = _isGradient.asStateFlow()

	private val _isAvatarGradient = MutableStateFlow(false)
	val isAvatarGradient: StateFlow<Boolean> = _isAvatarGradient.asStateFlow()

	private val _isNicknameColorful = MutableStateFlow(false)
	val isNicknameColorful: StateFlow<Boolean> = _isNicknameColorful.asStateFlow()

	private val _isAlterOutColor = MutableStateFlow(false)
	val isAlterOutColor: StateFlow<Boolean> = _isAlterOutColor.asStateFlow()

	private val _isUseDivider = MutableStateFlow(false)
	val isUseDivider: StateFlow<Boolean> = _isUseDivider.asStateFlow()

	private val sharedPreferences: SharedPreferences =
		contextParam.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)

	// -1 = system; matches PaletteEntity.id for saved palettes
	private val _selectedPaletteId = MutableStateFlow(
		sharedPreferences.getInt(Constants.SHARED_SELECTED_PALETTE_ID, -1)
	)
	val selectedPaletteId: StateFlow<Int> = _selectedPaletteId.asStateFlow()

	val allPalettes: StateFlow<List<PaletteEntity>> = paletteRepository.getAllPalettes()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	// Wallpaper States
	private val _wallpaperPreset = MutableStateFlow(
		runCatching {
			WallpaperPreset.valueOf(
				sharedPreferences.getString(Constants.SHARED_WALLPAPER_PRESET, WallpaperPreset.M3_EXPRESSIVE.name)
					?: WallpaperPreset.M3_EXPRESSIVE.name
			)
		}.getOrDefault(WallpaperPreset.M3_EXPRESSIVE)
	)
	val wallpaperPreset: StateFlow<WallpaperPreset> = _wallpaperPreset.asStateFlow()

	private val _wallpaperSeed = MutableStateFlow(
		sharedPreferences.getLong(Constants.SHARED_WALLPAPER_SEED, 0L)
	)
	val wallpaperSeed: StateFlow<Long> = _wallpaperSeed.asStateFlow()

	private val _isDarkWallpaperPreview = MutableStateFlow(false)
	val isDarkWallpaperPreview: StateFlow<Boolean> = _isDarkWallpaperPreview.asStateFlow()

	private val _isAttachWallpaperToTheme = MutableStateFlow(
		sharedPreferences.getBoolean(Constants.SHARED_WALLPAPER_ATTACH_TO_THEME, false)
	)
	val isAttachWallpaperToTheme: StateFlow<Boolean> = _isAttachWallpaperToTheme.asStateFlow()

	fun setWallpaperPreset(preset: WallpaperPreset) {
		_wallpaperPreset.value = preset
		_wallpaperSeed.value = 0L
		sharedPreferences.edit {
			putString(Constants.SHARED_WALLPAPER_PRESET, preset.name)
			putLong(Constants.SHARED_WALLPAPER_SEED, 0L)
		}
	}

	fun shuffleWallpaperSeed() {
		// Step by +5 (coprime to 12, and 5 % 4 == 1) so every single click is 100% guaranteed
		// to switch to a new macro layout (0 -> 1 -> 2 -> 3) while cycling all 12 color/layout states.
		val newSeed = (_wallpaperSeed.value + 5L) % 12L
		_wallpaperSeed.value = newSeed
		sharedPreferences.edit { putLong(Constants.SHARED_WALLPAPER_SEED, newSeed) }
	}

	fun toggleDarkWallpaperPreview(isDark: Boolean) {
		_isDarkWallpaperPreview.value = isDark
	}

	fun toggleAttachWallpaperToTheme(attach: Boolean) {
		_isAttachWallpaperToTheme.value = attach
		sharedPreferences.edit { putBoolean(Constants.SHARED_WALLPAPER_ATTACH_TO_THEME, attach) }
	}

	fun selectPalette(paletteId: Int) {
		_selectedPaletteId.value = paletteId
		sharedPreferences.edit {
			putInt(Constants.SHARED_SELECTED_PALETTE_ID, paletteId)
		}
	}

	fun deletePalette(palette: PaletteEntity) {
		viewModelScope.launch {
			paletteRepository.deletePalette(palette)
			if (_selectedPaletteId.value == palette.id) {
				selectPalette(-1)
			}
		}
	}

	fun getCurrentMonetColors(context: Context): Map<String, Int> {
		val palette = allPalettes.value.find { it.id == _selectedPaletteId.value }
		return if (palette != null) {
			val customScheme = ColorScheme.fromName(palette.scheme)
			generateMonetColors(palette.argbColor, customScheme)
		} else {
			getSystemMonetColors(context)
		}
	}

	fun saveWallpaper(context: Context) {
		CoroutineScope(Dispatchers.IO).launch {
			val colors = getCurrentMonetColors(context)
			val config = WallpaperConfig(
				preset = _wallpaperPreset.value,
				seed = _wallpaperSeed.value,
				isDark = _isDarkWallpaperPreview.value,
				isAmoled = _isAmoled.value,
			)
			val dm = context.resources.displayMetrics
			val width = maxOf(dm.widthPixels, 1080)
			val height = maxOf(dm.heightPixels, 2400)
			val bitmap = PixelWallpaperGenerator.renderToBitmap(width, height, colors, config)
			WallpaperStorage.saveWallpaperToStorage(
				context = context,
				bitmap = bitmap,
				isDark = _isDarkWallpaperPreview.value,
				presetName = _wallpaperPreset.value.name,
			)
		}
	}

	fun onShareTheme(context: Context, isLight: Boolean) {
		val inputFileName = resolveInputFileName(isLight)
		val outputFileName = resolveOutputFileName(isLight)

		val palette = allPalettes.value.find { it.id == _selectedPaletteId.value }
		val customSeedColor = palette?.argbColor ?: 0
		val customScheme = palette?.let { ColorScheme.fromName(it.scheme) } ?: ColorScheme.EXPRESSIVE

		CoroutineScope(Dispatchers.IO).launch {
			val colors = getCurrentMonetColors(context)
			val embeddedWallpaperBytes = if (_isAttachWallpaperToTheme.value) {
				val config = WallpaperConfig(
					preset = _wallpaperPreset.value,
					seed = _wallpaperSeed.value,
					isDark = !isLight,
					isAmoled = _isAmoled.value,
				)
				val dm = context.resources.displayMetrics
				val width = maxOf(dm.widthPixels, 1080)
				val height = maxOf(dm.heightPixels, 2400)
				val bitmap = PixelWallpaperGenerator.renderToBitmap(width, height, colors, config)
				WallpaperStorage.compressToJpegBytes(bitmap, quality = 92)
			} else null

			createTheme(
				context = context,
				isAmoled = _isAmoled.value,
				isGradient = _isGradient.value,
				isAvatarGradient = _isAvatarGradient.value,
				isNicknameColorful = _isNicknameColorful.value,
				isAlterOutColor = _isAlterOutColor.value,
				isUseDivider = _isUseDivider.value,
				isFrostedBubbles = _isFrostedBubbles.value,
				inputFileName = inputFileName,
				outputFileName = outputFileName,
				customSeedColor = customSeedColor,
				customScheme = customScheme,
				embeddedWallpaperBytes = embeddedWallpaperBytes,
			)
		}
	}

	fun onSaveTheme(context: Context, isLight: Boolean) {
		val inputFileName = resolveInputFileName(isLight)
		val outputFileName = resolveOutputFileName(isLight)

		val palette = allPalettes.value.find { it.id == _selectedPaletteId.value }
		val customSeedColor = palette?.argbColor ?: 0
		val customScheme = palette?.let { ColorScheme.fromName(it.scheme) } ?: ColorScheme.EXPRESSIVE

		CoroutineScope(Dispatchers.IO).launch {
			val colors = getCurrentMonetColors(context)
			val embeddedWallpaperBytes = if (_isAttachWallpaperToTheme.value) {
				val config = WallpaperConfig(
					preset = _wallpaperPreset.value,
					seed = _wallpaperSeed.value,
					isDark = !isLight,
					isAmoled = _isAmoled.value,
				)
				val dm = context.resources.displayMetrics
				val width = maxOf(dm.widthPixels, 1080)
				val height = maxOf(dm.heightPixels, 2400)
				val bitmap = PixelWallpaperGenerator.renderToBitmap(width, height, colors, config)
				WallpaperStorage.compressToJpegBytes(bitmap, quality = 92)
			} else null

			val themeBytes = buildThemeBytes(
				context = context,
				isAmoled = _isAmoled.value,
				isGradient = _isGradient.value,
				isAvatarGradient = _isAvatarGradient.value,
				isNicknameColorful = _isNicknameColorful.value,
				isAlterOutColor = _isAlterOutColor.value,
				isUseDivider = _isUseDivider.value,
				isFrostedBubbles = _isFrostedBubbles.value,
				inputFileName = inputFileName,
				customSeedColor = customSeedColor,
				customScheme = customScheme,
				embeddedWallpaperBytes = embeddedWallpaperBytes,
			)
			saveThemeBytesToDownloads(context, outputFileName, themeBytes)
		}
	}

	private fun resolveInputFileName(isLight: Boolean): String =
		if (isLight) Constants.INPUT_FILE_TELEGRAM_LIGHT else Constants.INPUT_FILE_TELEGRAM_DARK

	private fun resolveOutputFileName(isLight: Boolean): String = when {
		isLight -> Constants.OUTPUT_FILE_TELEGRAM_LIGHT
		!_isAmoled.value -> Constants.OUTPUT_FILE_TELEGRAM_DARK
		else -> Constants.OUTPUT_FILE_TELEGRAM_AMOLED
	}

	init {
		getSettings()
	}

	fun setSettings(settings: String, value: Boolean) {
		when (settings) {
			Constants.SHARED_IS_AMOLED -> _isAmoled.value = value
			Constants.SHARED_FROSTED_BUBBLES -> _isFrostedBubbles.value = value
			Constants.SHARED_USE_GRADIENT -> _isGradient.value = value
			Constants.SHARED_USE_COLORFUL_NICKNAME -> _isNicknameColorful.value = value
			Constants.SHARED_USE_GRADIENT_AVATARS -> _isAvatarGradient.value = value
			Constants.SHARED_USE_OLD_CHAT_STYLE -> _isAlterOutColor.value = value
			Constants.SHARED_USE_USE_DIVIDER -> _isUseDivider.value = value
		}
		sharedPreferences.edit { putBoolean(settings, value) }
	}

	private fun getSettings() {
		_isAmoled.value = sharedPreferences.getBoolean(Constants.SHARED_IS_AMOLED, false)
		_isFrostedBubbles.value = sharedPreferences.getBoolean(Constants.SHARED_FROSTED_BUBBLES, true)
		_isGradient.value = sharedPreferences.getBoolean(Constants.SHARED_USE_GRADIENT, false)
		_isAvatarGradient.value = sharedPreferences.getBoolean(Constants.SHARED_USE_GRADIENT_AVATARS, false)
		_isNicknameColorful.value = sharedPreferences.getBoolean(Constants.SHARED_USE_COLORFUL_NICKNAME, true)
		_isAlterOutColor.value = sharedPreferences.getBoolean(Constants.SHARED_USE_OLD_CHAT_STYLE, false)
		_isUseDivider.value = sharedPreferences.getBoolean(Constants.SHARED_USE_USE_DIVIDER, false)
	}
}
