package com.huawei.hms.ads.exoplayer.adapter;

import android.animation.LayoutTransition;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import com.google.android.exoplayer2.ui.PlayerView;
import com.huawei.hms.ads.exoplayer.adapter.enums.Placement;
import com.huawei.hms.ads.exoplayer.adapter.model.AdsInfo;
import com.huawei.hms.ads.exoplayer.adapter.ui.CustomInstreamView;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.instreamad.InstreamAd;
import com.huawei.hms.ads.instreamad.InstreamAdLoadListener;
import com.huawei.hms.ads.instreamad.InstreamAdLoader;
import com.huawei.hms.ads.instreamad.InstreamMediaChangeListener;
import com.huawei.hms.ads.instreamad.InstreamMediaStateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HwExoPlayerAdapter {


    private final String TAG = "RollAdsExoPlayer";


    private AppCompatActivity context;
    private PlayerView playerView;
    private String adUnitId;
    private String tcfConsent;
    private String appLanguage;
    private boolean isLocationCarried;
    private int coppa;
    private int underAgeConsent;
    private int nonPersonalizedValue;
    private int gender;
    private Location requestLocation;


    public static boolean lock = false;
    private static List<AdsInfo> adList;


    private CustomInstreamView instreamView;
    public static List<InstreamAd> adsToPlayed;
    InstreamAdLoader adLoader;

    boolean isPrerollShown = false;
    boolean isFirstQShown = false;
    boolean isMiddleShown = false;
    boolean isThirdQShown = false;
    boolean isPostRollShown = false;
    boolean isMarkersAdded = false;


    private HwExoPlayerAdapter(AppCompatActivity user_context) {

        this.context = user_context;
        HwAds.init(context);
        MultiDex.install(context);
    }

    public void loadRollAd(boolean skipInfo) {

        instreamView = new CustomInstreamView(context, skipInfo);
        context.getLifecycle().addObserver(instreamView);
        InstreamAdLoader.Builder builder = new InstreamAdLoader.Builder(this.context, adUnitId);
        adLoader = builder.setTotalDuration(15)
                .setMaxCount(1)
                .setInstreamAdLoadListener(new InstreamAdLoadListener() {
                    @Override
                    public void onAdLoaded(List<InstreamAd> ads) {
                        adsToPlayed = ads;
                        instreamView.setInstreamAds(adsToPlayed);
                        instreamView.inflateLayout();
                        Log.d(TAG, "Ad loaded Successfully");

                        instreamView.setInstreamMediaChangeListener(new InstreamMediaChangeListener() {
                            @Override
                            public void onSegmentMediaChange(InstreamAd ad) {

                                instreamView.setCallToActionView(ad.getWhyThisAd(), ad);
                            }
                        });

                        instreamView.setInstreamMediaStateListener(new InstreamMediaStateListener() {
                            @Override
                            public void onMediaProgress(int percent, int playTime) {
                                instreamView.updateAdDuration((adsToPlayed.get(0).getDuration() - playTime) / 1000);

                            }

                            @Override
                            public void onMediaStart(int playTime) {
                                Log.d(TAG, "Ad Media Started");
                                instreamView.updateAdDuration((adsToPlayed.get(0).getDuration() - playTime) / 1000);
                            }

                            @Override
                            public void onMediaPause(int playTime) {
                                Log.d(TAG, "Ad Media is Paused ");
                                instreamView.updateAdDuration((adsToPlayed.get(0).getDuration() - playTime) / 1000);
                            }

                            @Override
                            public void onMediaStop(int playTime) {
                                Log.d(TAG, "Ad Media is Stopped ");
                                instreamView.updateAdDuration((adsToPlayed.get(0).getDuration() - playTime) / 1000);
                            }

                            @Override
                            public void onMediaCompletion(int playTime) {
                                Log.d(TAG, "Ad Media is Completed");
                                playerView.removeView(instreamView);
                                Objects.requireNonNull(playerView.getPlayer()).play();
                                lock = false;
                            }

                            @Override
                            public void onMediaError(int playTime, int errorCode, int extra) {
                                Log.d(TAG, "onMediaError errorCode: " + errorCode);
                                playerView.removeView(instreamView);
                                Objects.requireNonNull(playerView.getPlayer()).play();
                                lock = false;
                            }
                        });

                    }

                    @Override
                    public void onAdFailed(int errorCode) {
                        Log.d(TAG, "onAdFailed errorCode: " + errorCode);
                        playerView.removeView(instreamView);
                        Objects.requireNonNull(playerView.getPlayer()).play();
                        lock = false;
                    }
                }).build();


    }

    private void addInstreamView() {
        ViewGroup.LayoutParams params = playerView.getLayoutParams();
        LayoutTransition transition = playerView.getLayoutTransition();
        instreamView.setLayoutParams(params);
        instreamView.setLayoutTransition(transition);
        playerView.addView(instreamView);

    }

    private void startAdListener() {
        final Handler handler = new Handler();
        Thread thread = new Thread() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (!isMarkersAdded && Objects.requireNonNull(playerView.getPlayer()).getContentDuration() > 0) {
                            addMarkers(Objects.requireNonNull(playerView.getPlayer()).getContentDuration());
                        }
                        if (!lock && !isPrerollShown) {
                            List<AdsInfo> tempList;
                            tempList = checkPositions(adList, Placement.PREROLL);
                            if (!tempList.isEmpty()) {
                                showRollAd(tempList.get(0).skippable);
                            }
                            isPrerollShown = true;

                        } else if (Objects.requireNonNull(playerView.getPlayer()).getCurrentPosition() >= (playerView.getPlayer().getContentDuration()) / 4 &&
                                !isFirstQShown && !lock && playerView.getPlayer().isPlaying()) {
                            List<AdsInfo> tempList;
                            tempList = checkPositions(adList, Placement.FIRST_QUARTILE);
                            if (!tempList.isEmpty()) {
                                showRollAd(tempList.get(0).skippable);
                            }
                            isFirstQShown = true;
                        } else if (Objects.requireNonNull(playerView.getPlayer()).getCurrentPosition() >= (playerView.getPlayer().getContentDuration()) / 2 &&
                                !isMiddleShown && !lock && playerView.getPlayer().isPlaying()) {
                            List<AdsInfo> tempList;
                            tempList = checkPositions(adList, Placement.MIDDLE);
                            if (!tempList.isEmpty()) {
                                showRollAd(tempList.get(0).skippable);
                            }
                            isMiddleShown = true;
                        } else if ((Objects.requireNonNull(playerView.getPlayer()).getCurrentPosition()) * 4 >= (playerView.getPlayer().getContentDuration()) * 3 &&
                                !isThirdQShown && !lock && playerView.getPlayer().isPlaying()) {
                            List<AdsInfo> tempList;
                            tempList = checkPositions(adList, Placement.THIRD_QUARTILE);
                            if (!tempList.isEmpty()) {
                                showRollAd(tempList.get(0).skippable);
                            }
                            isThirdQShown = true;
                        } else if (Objects.requireNonNull(playerView.getPlayer()).getCurrentPosition() >= (playerView.getPlayer().getContentDuration()) - 1000 &&
                                !isPostRollShown && !lock && playerView.getPlayer().isPlaying()) {
                            List<AdsInfo> tempList;
                            tempList = checkPositions(adList, Placement.POSTROLL);
                            if (!tempList.isEmpty()) {
                                showRollAd(tempList.get(0).skippable);
                            }
                            isPostRollShown = true;
                        }
                        handler.postDelayed(this, 1000);
                    }

                }, 1000);
            }
        };

        thread.start();
    }

    private List<AdsInfo> checkPositions(List<AdsInfo> list, Placement placement) {
        List<AdsInfo> temp = new ArrayList<>();
        for (AdsInfo ad : list) {
            if (ad.placement == placement) {
                temp.add(ad);
            }
        }
        return temp;
    }

    private void showRollAd(boolean skippable) {
        lock = true;
        loadRollAd(skippable);
        Objects.requireNonNull(playerView.getPlayer()).pause();
        addInstreamView();


        adLoader.loadAd(new AdParam.Builder().setTagForChildProtection(coppa).setConsent(tcfConsent)
                .setTagForUnderAgeOfPromise(underAgeConsent).setNonPersonalizedAd(nonPersonalizedValue)
                .setGender(gender).setRequestLocation(isLocationCarried).setAppLang(appLanguage)
                .setLocation(requestLocation)
                .build());

    }

    public static class Builder {

        private final AppCompatActivity context;
        private final PlayerView playerView;
        private String adUnitId;

        private int coppa = -1;
        private int underAgeConsent = -1;
        private int nonPersonalizedValue = 0;
        private int gender = 0;
        private String consent = null;
        private String language = null;
        private boolean isLocationCarried = true;
        private Location location = null;


        public Builder(AppCompatActivity context,
                       PlayerView playerView,
                       String adUnitId) {
            this.context = context;
            this.playerView = playerView;
            this.adUnitId = adUnitId;
            CustomInstreamView.player = playerView;
            adList = new ArrayList<>();

        }

        public Builder setAdItem(Placement placement, boolean skip) {
            adList.add(new AdsInfo(placement, skip));
            return this;
        }


        /**
         * @param consent is optional, use to set Tcf 2.0 consent
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setTcfConsent(String consent) {
            this.consent = consent;
            return this;
        }

        /**
         * @param location is optional, Sets the location information passed by your app.
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setLocation(Location location) {
            this.location = location;
            return this;
        }

        /**
         * @param locationValue is optional, use to carry location information for ad requests.
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder useLocationForRequest(boolean locationValue) {
            this.isLocationCarried = locationValue;
            return this;
        }

        /**
         * @param nonPersonalizedValue is optional, default value is 0. Use to allow only non-personalized ads
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setOnlyNonPersonalized(boolean nonPersonalizedValue) {
            if (nonPersonalizedValue)
                this.nonPersonalizedValue = 1;
            if (!nonPersonalizedValue)
                this.nonPersonalizedValue = 0;
            return this;
        }

        /**
         * @param language is optional. Use to make ad requests with users' language
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setAppLanguage(String language) {
            this.language = language;
            return this;
        }

        /**
         * @param coppa is optional, default value is -1. Use to allow for make ad requests according to COPPA
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setChildProtectionCOPPA(boolean coppa) {
            if (coppa)
                this.coppa = 1;
            if (!coppa)
                this.coppa = 0;
            return this;
        }

        /**
         * @param underAgeConsent is optional, default value is -1. Use to allow for make ad requests according to under age of consent.
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setUnderOfAgeConsent(boolean underAgeConsent) {
            if (underAgeConsent)
                this.underAgeConsent = 1;
            if (!underAgeConsent)
                this.underAgeConsent = 0;

            return this;
        }

        /**
         * @param gender is optional, default value is 0. Use to allow for make ad requests according to users' gender.
         * @implNote https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/adparam-builder-0000001050066829
         */
        public Builder setGender(int gender) {
            this.gender = gender;
            return this;
        }


        public void build() {
            HwExoPlayerAdapter player = new HwExoPlayerAdapter(context);
            player.playerView = this.playerView;
            player.adUnitId = this.adUnitId;
            player.tcfConsent = this.consent;
            player.isLocationCarried = this.isLocationCarried;
            player.coppa = this.coppa;
            player.underAgeConsent = this.underAgeConsent;
            player.gender = this.gender;
            player.appLanguage = this.language;
            player.requestLocation = this.location;
            player.nonPersonalizedValue = this.nonPersonalizedValue;
            player.startAdListener();
        }


    }

    private void addMarkers(long content) {

        int con = (int) content;
        int size = 0;
        long[] positions = new long[0];
        boolean[] booleanValues = new boolean[0];

        for (AdsInfo ad : adList) {
            if (ad.placement == Placement.PREROLL) {
                positions = addElementToArray(size, positions, 1000);
                booleanValues = addElementToBooleanArray(size, booleanValues);
                size++;
            }
            if (ad.placement == Placement.FIRST_QUARTILE) {
                positions = addElementToArray(size, positions, con / 4);
                booleanValues = addElementToBooleanArray(size, booleanValues);
                size++;
            }
            if (ad.placement == Placement.MIDDLE) {
                positions = addElementToArray(size, positions, con / 2);
                booleanValues = addElementToBooleanArray(size, booleanValues);
                size++;
            }
            if (ad.placement == Placement.THIRD_QUARTILE) {
                positions = addElementToArray(size, positions, (con * 3) / 4);
                ;
                booleanValues = addElementToBooleanArray(size, booleanValues);
                size++;
            }
            if (ad.placement == Placement.POSTROLL) {
                positions = addElementToArray(size, positions, con);
                booleanValues = addElementToBooleanArray(size, booleanValues);
                size++;
            }
        }
        playerView.setExtraAdGroupMarkers(positions, booleanValues);
        isMarkersAdded = true;
        Log.d(TAG, "Ad Markers added successfully.");
    }

    private static long[] addElementToArray(int n, long[] arr, int x) {
        long[] array = new long[n + 1];
        for (int i = 0; i < n; i++)
            array[i] = arr[i];
        array[n] = x;
        return array;
    }

    private static boolean[] addElementToBooleanArray(int n, boolean[] arr) {
        boolean[] array = new boolean[n + 1];
        for (int i = 0; i < n; i++)
            array[i] = arr[i];
        array[n] = false;
        return array;
    }
}

