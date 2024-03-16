package earth.mp3player.shared

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import earth.mp3player.models.Folder
import earth.mp3player.services.data.DataLoader
import earth.mp3player.services.data.DataManager
import earth.mp3player.services.playback.PlaybackController
import earth.mp3player.shared.utils.buildMediaItem


/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 *
 *
 * To implement a MediaBrowserService, you need to:
 *
 *  *  Extend [MediaBrowserServiceCompat], implementing the media browsing
 * related methods [MediaBrowserServiceCompat.onGetRoot] and
 * [MediaBrowserServiceCompat.onLoadChildren];
 *
 *  *  In onCreate, start a new [MediaSessionCompat] and notify its parent
 * with the session"s token [MediaBrowserServiceCompat.setSessionToken];
 *
 *  *  Set a callback on the [MediaSessionCompat.setCallback].
 * The callback will receive all the user"s actions, like play, pause, etc;
 *
 *  *  Handle all the actual music playing using any method your app prefers (for example,
 * [android.media.MediaPlayer])
 *
 *  *  Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * [MediaSessionCompat.setPlaybackState]
 * [MediaSessionCompat.setMetadata] and
 * [MediaSessionCompat.setQueue])
 *
 *  *  Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 *
 * To make your app compatible with Android Auto, you also need to:
 *
 *  *  Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 *
 */

class MP3PlayerCarMusicService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat
    private lateinit var playbackController: PlaybackController

    override fun onCreate() {
        super.onCreate()

        val className: String = this.javaClass.name.split(".").last()
        session = MediaSessionCompat(this, className)
        sessionToken = session.sessionToken
        session.setCallback(MP3PlayerCarCallBack)
        session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        //Init playback
        playbackController = PlaybackController.initInstance(baseContext)
        if (!DataLoader.isLoaded && !DataLoader.isLoading) {
            DataLoader.loadAllData(baseContext)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        session.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("root", null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        var children: MutableList<MediaItem>? = null
        when (parentId) {
            SCREEN_PAGES.ALL_FOLDERS.id -> {
                children = getFolderMediaItemList()
            }

            SCREEN_PAGES.ALL_ARTISTS.id -> {

            }

            SCREEN_PAGES.ALL_ALBUMS.id -> {

            }

            SCREEN_PAGES.ALL_GENRES.id -> {

            }

            SCREEN_PAGES.ALL_MUSICS.id -> {

            }

            SCREEN_PAGES.ROOT.id -> {
                children = getHomeScreen()
            }
        }
        if (children == null) {
            if (parentId.startsWith("${SCREEN_PAGES.ALL_FOLDERS.id}/")) {
                val folderId: Long = parentId.split("/").last().toLong()
                val folder: Folder = DataManager.folderMap[folderId]!!
                children = getFolderMediaItemList(folder = folder)
            }
        }
        result.sendResult(children)
    }

    private fun getHomeScreen(): MutableList<MediaItem> {
        val children: MutableList<MediaItem> = mutableListOf()
        for (page: SCREEN_PAGES in pages) {
            val mediaItem: MediaItem = buildMediaItem(
                id = page.id,
                description = page.description,
                title = page.title,
                flags = MediaItem.FLAG_BROWSABLE
            )
            children.add(mediaItem)
        }
        return children
    }

    /**
     * Get a list of media item based on the folder. If it is null, then return the root folders list
     *
     * @param folder the folder to get subfolders as media item, root folder if null
     */
    private fun getFolderMediaItemList(folder: Folder? = null): MutableList<MediaItem> {
        val mediaItemList: MutableList<MediaItem> = mutableListOf()
        val subfolderList: List<Folder> =
            folder?.getSubFolderList()?.values?.toList()
                ?: DataManager.rootFolderMap.values.toList()
        for (subFolder: Folder in subfolderList) {
            val mediaItem: MediaItem = buildMediaItem(
                id = subFolder.id.toString(),
                description = "Folder",
                title = subFolder.title,
                flags = MediaItem.FLAG_BROWSABLE
            )
            mediaItemList.add(mediaItem)
        }
        return mediaItemList
    }
}