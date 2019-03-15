package mmu.stu.ac.coursework.transport_finder_app.parser.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmu.stu.ac.coursework.transport_finder_app.model.Location;
import mmu.stu.ac.coursework.transport_finder_app.model.TransportLocation;

public class JsonParser {

    private BufferedReader br;

    public JsonParser(BufferedReader br){
        this.br = br;
    }

    public List<TransportLocation> getJson() {
        List<TransportLocation> list = new ArrayList<>();

        try{
            String line;
            while((line = br.readLine()) != null){
                JSONArray jsonArray = new JSONArray(line);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Location location = new Location()
                            .setLatitude(jsonObject.getString("latitude"))
                            .setLongitude(jsonObject.getString("longitude"));
                    TransportLocation transportLocation = new TransportLocation(
                            location, jsonObject.getString("country"),
                            jsonObject.getString("city"),
                            jsonObject.getString("timezone"),
                            jsonObject.getString("name"),
                            jsonObject.getString("type"));
                    list.add(transportLocation);
                }
            }

            return list;
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return list;

    }
}
