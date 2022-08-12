package ru.debajo.srrradio.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.ui.platform.LocalContext
import ru.debajo.srrradio.R
import ru.debajo.srrradio.icon.AppIcon

internal val DynamicThemeOptional: AppTheme?
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicTheme
        } else {
            null
        }
    }

@RequiresApi(Build.VERSION_CODES.S)
internal val DynamicTheme: AppTheme = AppTheme(
    code = "DynamicTheme",
    nameRes = R.string.theme_dynamic,
    colors = { dynamicDarkColorScheme(LocalContext.current) },
    icon = AppIcon.DYNAMIC,
)
