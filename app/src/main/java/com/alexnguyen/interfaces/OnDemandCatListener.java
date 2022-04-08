package com.alexnguyen.interfaces;

import com.alexnguyen.item.ItemOnDemandCat;

import java.util.ArrayList;

public interface OnDemandCatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}