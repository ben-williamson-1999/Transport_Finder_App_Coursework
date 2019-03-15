package mmu.stu.ac.coursework.transport_finder_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import mmu.stu.ac.coursework.transport_finder_app.distance.calculator.Haversine;
import mmu.stu.ac.coursework.transport_finder_app.model.Location;
import mmu.stu.ac.coursework.transport_finder_app.model.TransportLocation;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private List<TransportLocation> listItems;

    private MapView mapView;

    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private double lat;

    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.ListView);
        button = findViewById(R.id.button);
        this.mapView = (MapView) findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"}, 1);

        } else {
            LocationManager ls = (LocationManager) getSystemService(LOCATION_SERVICE);
            ls.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

                @Override
                public void onLocationChanged(android.location.Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    public void onClick(View v) {
        new GetDataClass().execute("http://zebedee.kriswelsh.com:8080/stations?latitude="+lat+"&longitude="+lng+"&type=" + getRadioButton());
    }

    private String getRadioButton() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.group);
        RadioButton airport = (RadioButton) findViewById(R.id.Airport);
        RadioButton station = (RadioButton) findViewById(R.id.Station);
        RadioButton port = (RadioButton) findViewById(R.id.Port);
        if (airport.isChecked()) {
            return "airport";
        } else if (station.isChecked()) {
            return "station";
        } else if (port.isChecked()) {
            return "port";
        } else {
            return "all";
        }
    }

    private void setListView(List<TransportLocation> items) {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<>());
        StringBuilder entry = new StringBuilder();
        for (TransportLocation tl : items) {
            arrayAdapter.clear();
            entry.append("Name: " + tl.getName() + "\nType: " + tl.getType() + "\nLatitude: " + tl.getLocation().getLatitude() + "\nLongitude: " + tl.getLocation().getLongitude() + "\n\n");
            arrayAdapter.add(entry.toString());
        }

        listView.setAdapter(arrayAdapter);


    }

    private void createMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.clear();

                mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("Current Location"));

                LocationEngine engine = new LocationEngineProvider(MainActivity.this).obtainBestLocationEngineAvailable();
                engine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
                engine.activate();
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                android.location.Location location = engine.getLastLocation();
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.0));
                mapboxMap.setCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(lat,lng)).build()
                );
                for(TransportLocation tl: listItems){
                    double distance = Haversine.harersine(lat, lng, tl.getLocation().getLatitude(),tl.getLocation().getLongitude());
                    BigDecimal bd = new BigDecimal(distance);
                    bd = bd.round(new MathContext(3));
                    double roundedDistance = bd.doubleValue();
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(tl.getLocation().getLatitude(),tl.getLocation().getLongitude()))
                            .title(tl.getName())
                            .snippet(String.valueOf(roundedDistance + "Km")));
                }

            }
        });
    }

    private class GetDataClass extends AsyncTask<String, Integer, Boolean>{

        StringBuilder string = new StringBuilder();

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                listItems= new ArrayList<>();
                URL url = new URL(strings[0]);
                URLConnection tc = url.openConnection();
                InputStreamReader isr = new InputStreamReader(tc.getInputStream());
                BufferedReader in = new BufferedReader(isr);

                String line;
                while((line = in.readLine()) != null){
                    JSONArray jsonArray = new JSONArray(line);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Location location = new Location()
                                .setLatitude(Double.parseDouble(jsonObject.getJSONObject("location").getString("latitude")))
                                .setLongitude(Double.parseDouble(jsonObject.getJSONObject("location").getString("longitude")));
                        TransportLocation transportLocation = new TransportLocation(
                                location,
                                jsonObject.getString("country"),
                                jsonObject.getString("city"),
                                jsonObject.getString("timezone"),
                                jsonObject.getString("name"),
                                jsonObject.getString("type"));
                        listItems.add(transportLocation);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return Boolean.TRUE;
        }

        protected void onPostExecute(Boolean bool){
            setListView(listItems);
            createMap();
        }
    }
}
