package com.alexnguyen.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexnguyen.radiofreeonline.R;
import com.alexnguyen.utils.Methods;
import com.alexnguyen.utils.SharedPref;

public class FragmentMain extends Fragment {

    SharedPref sharedPref;
    public static AppBarLayout appBarLayout;
    SearchView searchView;
    Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            FragmentHome f1 = new FragmentHome();
        ft.add(R.id.frame_content_home, f1, getString(R.string.home));
        ft.addToBackStack(getString(R.string.home));
        ft.commit();

        return rootView;
    }

}