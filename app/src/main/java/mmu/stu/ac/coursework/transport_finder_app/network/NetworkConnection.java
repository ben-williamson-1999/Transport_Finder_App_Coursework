package mmu.stu.ac.coursework.transport_finder_app.network;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mmu.stu.ac.coursework.transport_finder_app.parser.JsonParser.JsonParser;

public class NetworkConnection extends AsyncTask<String, Integer, BufferedReader> {

    @Override
    protected BufferedReader doInBackground(String[] strings) {

        BufferedReader in;

        try{

            URL url = new URL(strings[0]);
            URLConnection tc = url.openConnection();
            InputStreamReader isr = new InputStreamReader(tc.getInputStream());
            in = new BufferedReader(isr);

            return in;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(BufferedReader br) {
        new JsonParser(br);
    }
}
