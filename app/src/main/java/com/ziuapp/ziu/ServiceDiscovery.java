package com.ziuapp.ziu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.melnykov.fab.ObservableScrollView;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ziuapp.ziu.adapters.ServiceListAdapter;
import com.ziuapp.ziu.entities.Service;
import com.ziuapp.ziu.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Observable;

import android.os.Handler;


import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

import static com.ziuapp.ziu.utils.Utilities.customStyle;
import static com.ziuapp.ziu.utils.Utilities.share;


public class ServiceDiscovery extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {

        setDrawerHeaderImage(new ColorDrawable(getResources().getColor(R.color.theme_color)));

        // Categories
        MaterialSection trending = newSection("Trending", new PlaceholderFragment());
        MaterialSection appointment = newSection("Book Appointment", new PlaceholderFragment());
        MaterialSection delivery = newSection("Book Delivery", new PlaceholderFragment());


        // create sections
        this.addSection(customStyle(this,trending));
        this.addSection(customStyle(this,appointment));
        this.addSection(customStyle(this,delivery));

        // setting selected materal section, putting sections in an array
        //this.setSection();

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.closeDrawer();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Context mContext;
        private ProgressWheel mProgressWheel;
        private GridView mGridView;
        private ArrayList<Service> services;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_service_discovery, container, false);
            // Progress
            mProgressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);

            // Listview
            mGridView = (GridView) rootView.findViewById(R.id.gridview);
            setupGridView();
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mContext = activity;
        }

        // progress

        private void showProgressWheel() {
            mProgressWheel.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mProgressWheel.spin();
        }

        private void hideProgressWheel() {
            if (mProgressWheel.isSpinning())
                mProgressWheel.stopSpinning();
            mGridView.setVisibility(View.VISIBLE);
            mProgressWheel.setVisibility(View.GONE);
        }

        // listview

        private void setupGridView() {
            if (services == null) {
                if (!Utilities.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "Check Your Network Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressWheel();

                Ion
                        .with(mContext)
                        .load("https://api.github.com/users/voidabhi/repos")
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                hideProgressWheel();
                                if (e == null) {
                                    services = new ArrayList<Service>();
                                    for (int i = 0; i < result.size(); i++) {
                                        services.add(new Service(result.get(i).getAsJsonObject().get("name").getAsString(), "http://i.imgur.com/4GfhqNA.jpg"));
                                    }
                                    mGridView.setAdapter(new ServiceListAdapter(mContext, services));
                                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            startActivity(new Intent(getActivity(), Book.class));
                                        }
                                    });
                                } else {
                                    hideProgressWheel();
                                    Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            } else {
                mGridView.setAdapter(new ServiceListAdapter(mContext, services));
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(getActivity(), Book.class));
                    }
                });
            }
        }

    }

    }

