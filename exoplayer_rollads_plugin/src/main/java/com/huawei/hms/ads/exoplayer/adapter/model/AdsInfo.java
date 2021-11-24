package com.huawei.hms.ads.exoplayer.adapter.model;

import com.huawei.hms.ads.exoplayer.adapter.enums.Placement;

public class AdsInfo {

    public Placement placement;
    public Boolean skippable;

    public AdsInfo(Placement placement, Boolean skippable) {
        this.placement = placement;
        this.skippable = skippable;
    }
}
