package com.alexnguyen.interfaces;

import com.alexnguyen.item.ItemOnDemandCat;
import com.alexnguyen.item.ItemRadio;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRadio> arrayListFeatured, ArrayList<ItemRadio> arrayListMostViewed, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}