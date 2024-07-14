package io.yMusic.android.ui.screens.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.yMusic.android.LocalPlayerAwareWindowInsets
import io.yMusic.android.enums.ColorPaletteMode
import io.yMusic.android.enums.ColorPaletteName
import io.yMusic.android.enums.ThumbnailRoundness
import it.vfsfitvnm.vimusic.ui.components.themed.Header
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import io.yMusic.android.utils.applyFontPaddingKey
import io.yMusic.android.utils.colorPaletteModeKey
import io.yMusic.android.utils.colorPaletteNameKey
import io.yMusic.android.utils.isAtLeastAndroid13
import io.yMusic.android.utils.isShowingThumbnailInLockscreenKey
import io.yMusic.android.utils.rememberPreference
import io.yMusic.android.utils.thumbnailRoundnessKey
import io.yMusic.android.utils.useSystemFontKey

@ExperimentalAnimationApi
@Composable
fun AppearanceSettings() {
    val (colorPalette) = LocalAppearance.current

    var colorPaletteName by rememberPreference(colorPaletteNameKey, ColorPaletteName.Dynamic)
    var colorPaletteMode by rememberPreference(colorPaletteModeKey, ColorPaletteMode.System)
    var thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Light
    )
    var useSystemFont by rememberPreference(useSystemFontKey, false)
    var applyFontPadding by rememberPreference(applyFontPaddingKey, false)
    var isShowingThumbnailInLockscreen by rememberPreference(
        isShowingThumbnailInLockscreenKey,
        false
    )

    Column(
        modifier = Modifier
            .background(colorPalette.background0)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                    .asPaddingValues()
            )
    ) {
        Header(title = "Appearance")

        SettingsEntryGroupText(title = "COLORS")

        EnumValueSelectorSettingsEntry(
            title = "Theme",
            selectedValue = colorPaletteName,
            onValueSelected = { colorPaletteName = it }
        )

        EnumValueSelectorSettingsEntry(
            title = "Theme mode",
            selectedValue = colorPaletteMode,
            isEnabled = colorPaletteName != ColorPaletteName.PureBlack,
            onValueSelected = { colorPaletteMode = it }
        )

        SettingsGroupSpacer()

        SettingsEntryGroupText(title = "SHAPES")

        EnumValueSelectorSettingsEntry(
            title = "Thumbnail roundness",
            selectedValue = thumbnailRoundness,
            onValueSelected = { thumbnailRoundness = it },
            trailingContent = {
                Spacer(
                    modifier = Modifier
                        .border(width = 1.dp, color = colorPalette.accent,  shape = thumbnailRoundness.shape())
                        .background(color = colorPalette.background1, shape = thumbnailRoundness.shape())
                        .size(36.dp)
                )
            }
        )

        SettingsGroupSpacer()

        SettingsEntryGroupText(title = "TEXT")

        SwitchSettingEntry(
            title = "Use system font",
            text = "Use the font applied by the system",
            isChecked = useSystemFont,
            onCheckedChange = { useSystemFont = it }
        )

        SwitchSettingEntry(
            title = "Apply font padding",
            text = "Add spacing around texts",
            isChecked = applyFontPadding,
            onCheckedChange = { applyFontPadding = it }
        )

        if (!isAtLeastAndroid13) {
            SettingsGroupSpacer()

            SettingsEntryGroupText(title = "LOCKSCREEN")

            SwitchSettingEntry(
                title = "Show song cover",
                text = "Use the playing song cover as the lockscreen wallpaper",
                isChecked = isShowingThumbnailInLockscreen,
                onCheckedChange = { isShowingThumbnailInLockscreen = it }
            )
        }
    }
}
