package com.alexnguyen.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexnguyen.adapter.AdapterCityDetails;
import com.alexnguyen.asyncTasks.LoadRadioList;
import com.alexnguyen.interfaces.RadioListListener;
import com.alexnguyen.item.ItemRadio;
import com.alexnguyen.radiofreeonline.R;
import com.alexnguyen.utils.Constants;
import com.alexnguyen.utils.Methods;
import com.alexnguyen.utils.SharedPref;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class FragmentFeaturedRadio extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ItemRadio> arrayList;
    private AdapterCityDetails adapterCityDetails;
    private CircularProgressBar progressBar;
    private SearchView searchView;
    private Methods methods;
    private TextView textView_empty;
    public static Button button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    SharedPref sharedPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_city_detail, container, false);

        methods = new Methods(getActivity());
        sharedPref = new SharedPref(getActivity());
        arrayList = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.progressBar_city_details);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView = rootView.findViewById(R.id.recyclerView_city_detail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadFeaturedRadio();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeaturedRadio();
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search);
       // MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            if (!searchView.isIconified()) {
                adapterCityDetails.getFilter().filter(s);
                adapterCityDetails.notifyDataSetChanged();
            }
            return true;
        }
    };

    private void loadFeaturedRadio() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListRadio);
                                errr_msg = getString(R.string.items_not_found);
                                setAdapter();
                            } else {
                                errr_msg = message;
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                setEmpty();
                            }
                        } else {
                            errr_msg = getString(R.string.error_server);
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_FEATURED_RADIO, 0, "", "", Constants.search_text, "", "", "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            errr_msg = getString(R.string.internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapter() {
        adapterCityDetails = new AdapterCityDetails(getActivity(), arrayList);
        recyclerView.setAdapter(adapterCityDetails);
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}