package com.huawei.hms.ads.exoplayer.demo;


import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.huawei.hms.ads.exoplayer.adapter.enums.Placement;
import com.huawei.hms.ads.exoplayer.adapter.HwExoPlayerAdapter;

public class DemoActivity extends AppCompatActivity {

    private PlayerView playerView;
    private SimpleExoPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);

        playerView = findViewById(R.id.player_view);
        initializePlayer();
    }

    private void initializePlayer() {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));

        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(dataSourceFactory)
                        .setAdViewProvider(playerView);


        player = new SimpleExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build();
        playerView.setPlayer(player);


        Uri contentUri = Uri.parse("https://storage.googleapis.com/gvabox/media/samples/stock.mp4");
        MediaItem mediaItem = new MediaItem.Builder().setUri(contentUri).build();


        player.setMediaItem(mediaItem);
        player.prepare();


        new HwExoPlayerAdapter.Builder(DemoActivity.this, playerView, "testy3cglm3pj0")
                .setAdItem(Placement.PREROLL, true)
                .setAdItem(Placement.FIRST_QUARTILE, true)
                .setAdItem(Placement.MIDDLE, true)
                .setAdItem(Placement.THIRD_QUARTILE, true)
                .setAdItem(Placement.POSTROLL, true)
                .build();

        player.setPlayWhenReady(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (playerView != null) {
            playerView.onResume();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerView != null) {
            playerView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}