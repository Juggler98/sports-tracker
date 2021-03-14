package com.example.sportstracker.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.sportstracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        mapView = view.findViewById(R.id.mapView3);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int mapType = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.mapTypePref), "0"));
        switch (mapType) {
            case 0:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 3:
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        mapView.onStart();
    }

    @Override
    public void onDestroy() {
        gMap = null;
        mapView.onDestroy();
        super.onDestroy();
        Log.d("LC_MapFragment", "MapFragment OnDestroy");
    }

}
