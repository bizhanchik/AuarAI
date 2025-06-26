package com.bizhan.auarai.fragments.map;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.bizhan.auarai.API.APIFetcher;
import com.bizhan.auarai.API.GetAPI;
import com.bizhan.auarai.API.auth.Login;
import com.bizhan.auarai.API.gemini.GeminiAdvice;
import com.bizhan.auarai.API.openWeatherMapAPI.GetWeather;
import com.bizhan.auarai.API.openWeatherMapAPI.WeatherFetcher;
import com.bizhan.auarai.models.WeatherData;
import com.bizhan.auarai.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String token;

    APIFetcher fetcher = APIFetcher.getInstance();
    WeatherFetcher weatherFetcher = WeatherFetcher.getInstance();
    boolean success;
    private LocationManager locationManager;
    private Marker tempMarker;
    private GoogleMap mMap;

    public MapFragment() {
        super(R.layout.fragment_map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        token = Login.getToken(requireContext());
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        requestApiKeys();
    }

    private void requestApiKeys() {
        GetAPI getAPI = new GetAPI(requireContext(), token, fetcher);
        getAPI.fetch(new GetAPI.APIResultCallback() {
            @Override
            public void onSuccess(boolean parsed) {
                if (!parsed) {
                    Toast.makeText(requireContext(), "Ошибка парсинга JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("API_ERROR", "Ошибка при получении данных: " + message);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Ошибка соединения", Toast.LENGTH_SHORT).show();

                    }
                    );
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;



        mMap.setOnMarkerClickListener(marker -> {
            showBottomSheet(marker);
            return true;
        });

        mMap.setOnMapClickListener(latLng -> {
            if (tempMarker != null) tempMarker.remove();

            tempMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Точка нажатия"));
            if (tempMarker != null) {
                showBottomSheet(tempMarker);
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15.0f));
            }
        } else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            try {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));
                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
    }

    private void showBottomSheet(Marker clickedMarker) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);

        String weatherKey = fetcher.getOpenWeatherMapApi();


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView tempText = bottomSheetDialog.findViewById(R.id.tempText);
        TextView cityNameText = bottomSheetDialog.findViewById(R.id.cityName);
        TextView windText = bottomSheetView.findViewById(R.id.windSpeed);
        TextView humidityText = bottomSheetView.findViewById(R.id.humidityText);
        TextView visibilityText = bottomSheetDialog.findViewById(R.id.visibilityText);
        TextView description = bottomSheetDialog.findViewById(R.id.descriptionText);
        TextView maxTemp = bottomSheetDialog.findViewById(R.id.maxTemp);
        TextView minTemp = bottomSheetDialog.findViewById(R.id.minTemp);
        TextView feelsLike = bottomSheetDialog.findViewById(R.id.feelsLike);
        TextView aiAdvice = bottomSheetDialog.findViewById(R.id.aiAdvice);
        ImageView weatherImg = bottomSheetView.findViewById(R.id.weatherImg);





        GetWeather.getWeatherByCoords(
                clickedMarker.getPosition().latitude,
                clickedMarker.getPosition().longitude,
                weatherKey,
                new GetWeather.WeatherDualCallback() {
                    @Override
                    public void onSuccess(String currentWeather, String forecastWeather) {
                        requireActivity().runOnUiThread(() -> {
                            WeatherData weather = weatherFetcher.fetchWeatherAPI(currentWeather, forecastWeather);
                            if (weather != null) {
                                tempText.setText(String.format(Locale.getDefault(), "%.0f°", weather.currentTemp));
                                feelsLike.setText(feelsLike.getText() + String.format(Locale.getDefault(), "%.0f°", weather.feelsLike));
                                humidityText.setText(String.format(Locale.getDefault(), "%d%%", weather.humidity));
                                visibilityText.setText(weather.visibility >= 0
                                        ? String.format(Locale.getDefault(), "%.0f км", weather.visibility / 1000.0)
                                        : "-");
                                windText.setText(String.format(Locale.getDefault(), "%.1f м/с", weather.windSpeed));
                                description.setText(capitalize(weather.description));
                                maxTemp.setText(maxTemp.getText() + String.format(Locale.getDefault(), "%.0f°", weather.maxTempToday));
                                minTemp.setText(minTemp.getText() + String.format(Locale.getDefault(), "%.0f°", weather.minTempToday));
                                cityNameText.setText(weather.cityName);
                                String iconCode = weather.icon;
                                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";

                                Glide.with(requireContext())
                                        .load(iconUrl)
                                        .into(weatherImg);

                            } else {
                                tempText.setText("–");
                            }

                            JSONObject geminiBody = new JSONObject();
                            JSONObject weatherData = new JSONObject();
                            try {
                                weatherData.put("temperature", weather.currentTemp);
                                weatherData.put("condition", weather.description);
                                weatherData.put("humidity", weather.humidity);
                                weatherData.put("windSpeed", weather.windSpeed);
                                weatherData.put("cityName", weather.cityName);
                                geminiBody.put("weatherData", weatherData);
                            }catch (JSONException e){
                                Toast.makeText(requireContext(), e.getMessage(), LENGTH_LONG).show();
                            }

                            GeminiAdvice geminiAdvice = new GeminiAdvice(requireContext(), token, geminiBody);
                            geminiAdvice.getGeminiAdvice(new GeminiAdvice.GeminiCallback() {
                                @Override
                                public void onSuccess(String answer) {
                                    if (isAdded() && getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            aiAdvice.setText(answer);
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    requireActivity().runOnUiThread(() -> {
                                        aiAdvice.setText("Ai error: " + message);
                                    });
                                }
                            });

                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Ошибка погоды: " + error, LENGTH_LONG).show()
                        );
                    }
                }
        );


        FrameLayout bottomSheetBehavior = bottomSheetDialog.findViewById(
                com.google.android.material.R.id.design_bottom_sheet
        );

        if (bottomSheetBehavior != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetBehavior);
            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        if (tempMarker != null) {
                            tempMarker.remove();
                            tempMarker = null;
                        }
                    }
                }
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (tempMarker != null) {
                tempMarker.remove();
                tempMarker = null;
            }
        });
    }
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

}
