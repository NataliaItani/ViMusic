package io.yMusic.android.utils

import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import io.yMusic.innertube.Innertube
import it.vfsfitvnm.innertube.models.bodies.ContinuationBody
import io.yMusic.innertube.requests.playlistPage
import io.yMusic.innertube.utils.plus
import io.yMusic.android.models.Song

val Innertube.SongItem.asMediaItem: MediaItem
    get() = MediaItem.Builder()
        .setMediaId(key)
        .setUri(key)
        .setCustomCacheKey(key)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(info?.name)
                .setArtist(authors?.joinToString("") { it.name ?: "" })
                .setAlbumTitle(album?.name)
                .setArtworkUri(thumbnail?.url?.toUri())
                .setExtras(
                    bundleOf(
                        "albumId" to album?.endpoint?.browseId,
                        "durationText" to durationText,
                        "artistNames" to authors?.filter { it.endpoint != null }?.mapNotNull { it.name },
                        "artistIds" to authors?.mapNotNull { it.endpoint?.browseId },
                    )
                )
                .build()
        )
        .build()

val Innertube.VideoItem.asMediaItem: MediaItem
    get() = MediaItem.Builder()
        .setMediaId(key)
        .setUri(key)
        .setCustomCacheKey(key)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(info?.name)
                .setArtist(authors?.joinToString("") { it.name ?: "" })
                .setArtworkUri(thumbnail?.url?.toUri())
                .setExtras(
                    bundleOf(
                        "durationText" to durationText,
                        "artistNames" to if (isOfficialMusicVideo) authors?.filter { it.endpoint != null }?.mapNotNull { it.name } else null,
                        "artistIds" to if (isOfficialMusicVideo) authors?.mapNotNull { it.endpoint?.browseId } else null,
                    )
                )
                .build()
        )
        .build()

val Song.asMediaItem: MediaItem
    get() = MediaItem.Builder()
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artistsText)
                .setArtworkUri(thumbnailUrl?.toUri())
                .setExtras(
                    bundleOf(
                        "durationText" to durationText
                    )
                )
                .build()
        )
        .setMediaId(id)
        .setUri(id)
        .setCustomCacheKey(id)
        .build()

fun String?.thumbnail(size: Int): String? {
    return when {
        this?.startsWith("https://lh3.googleusercontent.com") == true -> "$this-w$size-h$size"
        this?.startsWith("https://yt3.ggpht.com") == true -> "$this-w$size-h$size-s$size"
        else -> this
    }
}

fun Uri?.thumbnail(size: Int): Uri? {
    return toString().thumbnail(size)?.toUri()
}

fun formatAsDuration(millis: Long) = DateUtils.formatElapsedTime(millis / 1000).removePrefix("0")

suspend fun Result<Innertube.PlaylistOrAlbumPage>.completed(): Result<Innertube.PlaylistOrAlbumPage>? {
    var playlistPage = getOrNull() ?: return null

    while (playlistPage.songsPage?.continuation != null) {
        val continuation = playlistPage.songsPage?.continuation!!
        val otherPlaylistPageResult = io.innertube.requests.playlistPage(
            ContinuationBody(
                continuation = continuation
            )
        ) ?: break

        if (otherPlaylistPageResult.isFailure) break

        otherPlaylistPageResult.getOrNull()?.let { otherSongsPage ->
            playlistPage = playlistPage.copy(songsPage = playlistPage.songsPage + otherSongsPage)
        }
    }

    return Result.success(playlistPage)
}

inline val isAtLeastAndroid6
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

inline val isAtLeastAndroid8
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

inline val isAtLeastAndroid12
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

inline val isAtLeastAndroid13
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
