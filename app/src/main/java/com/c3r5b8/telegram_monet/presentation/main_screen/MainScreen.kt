package com.c3r5b8.telegram_monet.presentation.main_screen

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.c3r5b8.telegram_monet.R
import com.c3r5b8.telegram_monet.common.Constants
import com.c3r5b8.telegram_monet.data.local.entity.PaletteEntity
import com.c3r5b8.telegram_monet.presentation.main_screen.components.AboutCard
import com.c3r5b8.telegram_monet.presentation.main_screen.components.CreateThemeCard
import com.c3r5b8.telegram_monet.presentation.main_screen.components.PaletteCard
import com.c3r5b8.telegram_monet.presentation.main_screen.components.PixelWallpaperCard
import com.c3r5b8.telegram_monet.presentation.main_screen.components.SettingsCard
import com.c3r5b8.telegram_monet.presentation.main_screen.components.TopAppBar
import com.c3r5b8.telegram_monet.wallpaper.WallpaperPreset
import androidx.core.net.toUri
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
	onOpenImagePalette: () -> Unit = {},
	onEditPalette: (Int) -> Unit = {},
	viewModel: MainScreenViewModel = koinViewModel(),
) {
	val isAmoled by viewModel.isAmoled.collectAsState()
	val isFrostedBubbles by viewModel.isFrostedBubbles.collectAsState()
	val isGradient by viewModel.isGradient.collectAsState()
	val isAvatarGradient by viewModel.isAvatarGradient.collectAsState()
	val isNicknameColorful by viewModel.isNicknameColorful.collectAsState()
	val isAlterOutColor by viewModel.isAlterOutColor.collectAsState()
	val isUseDivider by viewModel.isUseDivider.collectAsState()
	val allPalettes by viewModel.allPalettes.collectAsState()
	val selectedPaletteId by viewModel.selectedPaletteId.collectAsState()

	val wallpaperPreset by viewModel.wallpaperPreset.collectAsState()
	val wallpaperSeed by viewModel.wallpaperSeed.collectAsState()
	val isDarkWallpaperPreview by viewModel.isDarkWallpaperPreview.collectAsState()

	val context = LocalContext.current
	val currentColors = remember(selectedPaletteId, allPalettes) {
		viewModel.getCurrentMonetColors(context)
	}

	MainScreenComponent(
		isAmoled = isAmoled,
		isFrostedBubbles = isFrostedBubbles,
		isGradient = isGradient,
		isAvatarGradient = isAvatarGradient,
		isNicknameColorful = isNicknameColorful,
		isAlterOutColor = isAlterOutColor,
		isUseDivider = isUseDivider,
		allPalettes = allPalettes,
		selectedPaletteId = selectedPaletteId,
		currentColors = currentColors,
		wallpaperPreset = wallpaperPreset,
		wallpaperSeed = wallpaperSeed,
		isDarkWallpaperPreview = isDarkWallpaperPreview,
		setAmoled = { viewModel.setSettings(Constants.SHARED_IS_AMOLED, it) },
		setFrostedBubbles = { viewModel.setSettings(Constants.SHARED_FROSTED_BUBBLES, it) },
		setGradient = { viewModel.setSettings(Constants.SHARED_USE_GRADIENT, it) },
		setAvatarGradient = { viewModel.setSettings(Constants.SHARED_USE_GRADIENT_AVATARS, it) },
		setNicknameColorful = { viewModel.setSettings(Constants.SHARED_USE_COLORFUL_NICKNAME, it) },
		setUseOldChatStyle = { viewModel.setSettings(Constants.SHARED_USE_OLD_CHAT_STYLE, it) },
		setUseDivider = { viewModel.setSettings(Constants.SHARED_USE_USE_DIVIDER, it) },
		onSelectPalette = { viewModel.selectPalette(it) },
		onDeletePalette = { viewModel.deletePalette(it) },
		onEditPalette = onEditPalette,
		onApplyTheme = { isLight -> viewModel.onShareTheme(context, isLight) },
		onSaveTheme = { isLight -> viewModel.onSaveTheme(context, isLight) },
		onSelectWallpaperPreset = { viewModel.setWallpaperPreset(it) },
		onShuffleWallpaper = { viewModel.shuffleWallpaperSeed() },
		onToggleDarkWallpaperPreview = { viewModel.toggleDarkWallpaperPreview(it) },
		onSaveWallpaper = { viewModel.saveWallpaper(context) },
		onOpenImagePalette = onOpenImagePalette,
	)
}

@Composable
private fun MainScreenComponent(
	isAmoled: Boolean,
	isFrostedBubbles: Boolean,
	isGradient: Boolean,
	isAvatarGradient: Boolean,
	isNicknameColorful: Boolean,
	isAlterOutColor: Boolean,
	isUseDivider: Boolean,
	allPalettes: List<PaletteEntity>,
	selectedPaletteId: Int,
	currentColors: Map<String, Int>,
	wallpaperPreset: WallpaperPreset,
	wallpaperSeed: Long,
	isDarkWallpaperPreview: Boolean,
	setAmoled: (value: Boolean) -> Unit,
	setFrostedBubbles: (value: Boolean) -> Unit,
	setGradient: (value: Boolean) -> Unit,
	setAvatarGradient: (value: Boolean) -> Unit,
	setNicknameColorful: (value: Boolean) -> Unit,
	setUseOldChatStyle: (value: Boolean) -> Unit,
	setUseDivider: (value: Boolean) -> Unit,
	onSelectPalette: (Int) -> Unit,
	onDeletePalette: (PaletteEntity) -> Unit,
	onEditPalette: (Int) -> Unit,
	onApplyTheme: (isLight: Boolean) -> Unit,
	onSaveTheme: (isLight: Boolean) -> Unit,
	onSelectWallpaperPreset: (WallpaperPreset) -> Unit,
	onShuffleWallpaper: () -> Unit,
	onToggleDarkWallpaperPreview: (Boolean) -> Unit,
	onSaveWallpaper: () -> Unit,
	onOpenImagePalette: () -> Unit,
) {
	val scrollBehavior =
		TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

	val localContext = LocalContext.current

	Scaffold(
		modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
		topBar = {
			TopAppBar(scrollBehavior = scrollBehavior) {
				val i = Intent(Intent.ACTION_VIEW)
				i.data = Constants.URL_ABOUT.toUri()
				localContext.startActivity(i)
			}
		}
	) { pad ->

		LazyVerticalStaggeredGrid(
			modifier = Modifier
				.fillMaxSize()
				.padding(pad),
			columns = StaggeredGridCells.Adaptive(400.dp),
		) {

			item {
				CreateThemeCard(
					title = R.string.light_theme,
					description = R.string.light_theme_description,
					icon = R.drawable.theme_icon_light,
					onApplyClick = { onApplyTheme(true) },
					onSaveClick = { onSaveTheme(true) },
				)
			}

			item {
				CreateThemeCard(
					title = R.string.dark_theme,
					description = R.string.dark_theme_description,
					icon = R.drawable.theme_icon_dark,
					onApplyClick = { onApplyTheme(false) },
					onSaveClick = { onSaveTheme(false) },
				)
			}

			item {
				PixelWallpaperCard(
					colors = currentColors,
					preset = wallpaperPreset,
					seed = wallpaperSeed,
					isDarkPreview = isDarkWallpaperPreview,
					isAmoled = isAmoled,
					onSelectPreset = onSelectWallpaperPreset,
					onShuffle = onShuffleWallpaper,
					onToggleDarkPreview = onToggleDarkWallpaperPreview,
					onSaveWallpaper = onSaveWallpaper,
				)
			}

			item {
				PaletteCard(
					allPalettes = allPalettes,
					selectedPaletteId = selectedPaletteId,
					onSelectPalette = onSelectPalette,
					onDeletePalette = onDeletePalette,
					onEditPalette = { onEditPalette(it.id) },
					onOpenImagePalette = onOpenImagePalette,
				)
			}

			item {
				SettingsCard(
					isAmoled = isAmoled,
					isFrostedBubbles = isFrostedBubbles,
					isGradient = isGradient,
					isAvatarGradient = isAvatarGradient,
					isNicknameColorful = isNicknameColorful,
					isAlterOutColor = isAlterOutColor,
					isUseDivider = isUseDivider,
					setAmoled = setAmoled,
					setFrostedBubbles = setFrostedBubbles,
					setGradient = setGradient,
					setAvatarGradient = setAvatarGradient,
					setNicknameColorful = setNicknameColorful,
					setUseAlterOutColor = setUseOldChatStyle,
					setUseDivider = setUseDivider,
				)
			}

			item {
				AboutCard()
			}
		}
	}
}

@Preview
@Composable
private fun MainScreenPreview() {
	MainScreenComponent(
		isAmoled = true,
		isFrostedBubbles = true,
		isGradient = false,
		isAvatarGradient = true,
		isNicknameColorful = false,
		isAlterOutColor = true,
		isUseDivider = false,
		allPalettes = emptyList(),
		selectedPaletteId = -1,
		currentColors = emptyMap(),
		wallpaperPreset = WallpaperPreset.PIXEL_CLASSIC,
		wallpaperSeed = 42L,
		isDarkWallpaperPreview = false,
		setAmoled = {},
		setFrostedBubbles = {},
		setGradient = {},
		setAvatarGradient = {},
		setNicknameColorful = {},
		setUseOldChatStyle = {},
		setUseDivider = {},
		onSelectPalette = {},
		onDeletePalette = {},
		onEditPalette = {},
		onApplyTheme = { _ -> },
		onSaveTheme = { _ -> },
		onSelectWallpaperPreset = {},
		onShuffleWallpaper = {},
		onToggleDarkWallpaperPreview = {},
		onSaveWallpaper = {},
		onOpenImagePalette = {},
	)
}
