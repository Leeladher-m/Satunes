/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.components.settings

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.R
import earth.mp3player.ui.components.settings.utils.openUrl
import earth.mp3player.ui.utils.showToast

/**
 * @author Antoine Pirlot on 10/04/2024
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tipeee(
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val context: Context = LocalContext.current
    val tipeeeUrl: String = "https://fr.tipeee.com/antoinepirlot"
    Image(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { openUrl(context = context, url = tipeeeUrl) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showToast(context = context, message = tipeeeUrl)
                }
            ),
        painter = painterResource(id = R.drawable.tipeee_logo),
        contentDescription = "Tipeee Logo",
    )
}

@Preview
@Composable
fun TipeeePreview() {
    Tipeee()
}