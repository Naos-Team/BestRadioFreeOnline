package com.alexnguyen.interfaces;

import com.alexnguyen.item.ItemTheme;

import java.util.ArrayList;

public interface ThemeListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemTheme> arrayListTheme);
}