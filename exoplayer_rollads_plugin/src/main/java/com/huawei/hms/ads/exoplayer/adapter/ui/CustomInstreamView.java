package com.huawei.hms.ads.exoplayer.adapter.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.exoplayer2.ui.PlayerView;
import com.huawei.hms.ads.exoplayer_rollads_plugin.R;
import com.huawei.hms.ads.exoplayer.adapter.HwExoPlayerAdapter;
import com.huawei.hms.ads.instreamad.InstreamAd;
import com.huawei.hms.ads.instreamad.InstreamView;

import java.util.Objects;


public class CustomInstreamView extends InstreamView implements LifecycleObserver {


    @SuppressLint("StaticFieldLeak")
    public static PlayerView player;

    private final String TAG = "CustomInstreamView";

    private final Context context;
    private TextView tvSkipAd;
    private TextView tvAdFlag;
    private TextView tvLearnMore;
    private TextView tvCountDown;
    private ImageView ivWhyThisAd;
    private ImageView ivMute;
    private boolean skipValue;


    private Boolean isMute = false;


    public CustomInstreamView(Context context, boolean skippable) {
        super(context);
        this.context = context;
        this.skipValue = skippable;
    }


    private void initUIElements() {
        tvSkipAd = findViewById(R.id.instream_skip);
        tvAdFlag = findViewById(R.id.instream_ad_flag);
        tvLearnMore = findViewById(R.id.instream_call_to_action);
        ivWhyThisAd = findViewById(R.id.instream_why_this_ad);
        ivMute = findViewById(R.id.mic_icon);
        tvCountDown = findViewById(R.id.instream_count_down);

        if (!skipValue) {
            tvSkipAd.setVisibility(View.GONE);
        }
        initListeners();
    }

    private void initListeners() {

        tvSkipAd.setOnClickListener(view -> {

            Objects.requireNonNull(player.getPlayer()).play();
            this.onClose();
            this.destroy();
            this.setVisibility(View.GONE);
            HwExoPlayerAdapter.lock = false;

        });

        tvAdFlag.setOnClickListener(view -> {
            Log.i(TAG, "initListeners: Ad Flag Clicked!");
        });

        tvLearnMore.setOnClickListener(view -> {
            Log.i(TAG, "initListeners: Learn More Clicked!");


        });

        ivWhyThisAd.setOnClickListener(view -> {
            Log.i(TAG, "initListeners: Why This Ad Clicked!");


        });

        ivMute.setOnClickListener(view -> {
            if (isMute) {
                unmute();
                ivMute.setBackgroundResource(R.drawable.ad_mic_on);
                isMute = false;
            } else {
                mute();
                ivMute.setBackgroundResource(R.drawable.ad_mic_off);
                isMute = true;
            }
            Log.i(TAG, "initListeners: Mute Icon Clicked!");
        });

    }

    @SuppressLint("SetTextI18n")
    public void updateAdDuration(Long duration) {
        tvCountDown.setText(duration + "s");
    }

    public void inflateLayout() {
        inflate(context, R.layout.custom_roll_ads, this);
        initUIElements();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        pauseView();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        resumeView();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        destroy();
    }

    public void setCallToActionView(String whyThisAdUrl, InstreamAd ad) {

        if (!TextUtils.isEmpty(whyThisAdUrl)) {
            ivWhyThisAd.setVisibility(View.VISIBLE);
            ivWhyThisAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(whyThisAdUrl)));
                }
            });
        } else {
            ivWhyThisAd.setVisibility(View.GONE);
        }

        String cta = ad.getCallToAction();
        if (!TextUtils.isEmpty(cta)) {
            tvLearnMore.setVisibility(View.VISIBLE);
            tvLearnMore.setText(cta);
            this.setCallToActionView(tvLearnMore);
        }
    }
}
