package mmu.stu.ac.coursework.transport_finder_app;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private TextView textView;

    private ArrayList<String> listItems = new ArrayList<>();

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(53.47,-2.23)).build()
                );

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(53.47, -2.23))
                        .title("Test")
                        .snippet("Test2"));


            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
    }

    public void onClick(View v){
        new GetDataClass().execute("http://zebedee.kriswelsh.com:8080/stations?latitude=53.472&longitude=-2.244&type=station");
    }

    private class GetDataClass extends AsyncTask<String, Integer, String>{

        String parsedJson = "";
        StringBuilder string = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(strings[0]);
                URLConnection tc = url.openConnection();
                InputStreamReader isr = new InputStreamReader(tc.getInputStream());
                BufferedReader in = new BufferedReader(isr);

                String line;
                while((line = in.readLine()) != null){
                    JSONArray ja = new JSONArray(line);
                    for(int i=0;i<ja.length();i++){
                        JSONObject jo = (JSONObject) ja.get(i);
                        listItems.add(jo.getString("name"));
                    }
                }
                textView = findViewById(R.id.textView);
                for(String s: listItems){
                    string.append(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return string.toString();
        }

        protected void onPostExecute(String string){
            textView.findViewById(R.id.textView);
            textView.setText(string);
        }
    }
}
