package com.example.trafficscotland;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.*;

public class roadmap extends FragmentActivity implements OnMapReadyCallback {
    private TextView rawDataDisplay;
    private String result;
    private String trimmed;
    private String removedrss;
    Button button;


    ArrayList<ArrayList<String>> mapcoordinates = new ArrayList<ArrayList<String>>();
    ArrayList<String> n = new ArrayList<String>();

    double pointV[] = {};
    double pointU[] = {};



    private Button startButton;

    // Traffic Scotland URLs
    private String urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap);


        startProgress();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    //this part is parser and stuff

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new roadmap.Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;
        public String data;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    trimmed = result.replace("null", "");
                    removedrss = trimmed.replace("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>", "");
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }
            roadmap.this.runOnUiThread(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                public void parserrunner() {
                    try {




                        HashMap<String, String> item = new HashMap<>();
                        ListView lv = findViewById(R.id.listView);
                        InputStream inputStream = getAssets().open("roadworks.xml");
                        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserFactory.newPullParser();
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                        InputStreamReader isReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(isReader);
                        StringBuffer sb = new StringBuffer();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            sb.append(str);
                        }
                        String parsedobjectdata = sb.toString();
                        String removeparserss = parsedobjectdata.replace("<rss xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"2.0\">", "");
                        Log.e("MyTag", parsedobjectdata);
                        parser.setInput(new StringReader(removedrss));
                        String tag = "", text = "";
                        int event = parser.getEventType();
                        while (event != XmlPullParser.END_DOCUMENT) {
                            tag = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    if (tag.equals("item"))
                                        n = new ArrayList<>();
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;
                                case XmlPullParser.END_TAG:
                                    switch (tag) {
                                        case "title":
                                            String info = "title";

                                            break;
                                        case "description":
                                            String name = "description";
                                            String newname = name.replaceAll("<br />", "$1F$2");

                                            break;

                                        case "georss:point":
                                            String data = "".replace("=", "");
                                            n.add(text);
                                            break;
                                        case "pubDate":

                                            break;
                                        case "item":
                                            if (item != null) {
                                                mapcoordinates.add(n);
                                            }
                                            break;
                                    }
                                    break;
                            }
                            event = parser.next();
                        }


                        for (int j = 0; j < mapcoordinates.size(); j++){
                            System.out.println(mapcoordinates);

                        }

                    } catch (XmlPullParserException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }




                @RequiresApi(api = Build.VERSION_CODES.N)
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    String data = removedrss;
                    //here is going to be the pull parser inserted
                    parserrunner();


                }

            });


        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        GoogleMap mMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map);
        googleMap.setMapStyle(mapStyleOptions);



        // Add a marker in Sydney and move the camera
        double pointX[] = {
                55.8642,
                56.0367767647623,
                56.0732980724831,
                56.0847247150839,
                56.0201823620682,
                55.9842626977915,
                56.2031622842013,
                57.4822126713545,
                57.5291971924401,
                56.5266503848028,
                56.3746704265705,
                57.4699033735138,
                56.44033764161

        };


        double pointY[] = {

                -4.2518
                -3.40822032632123,
                -3.38885965373524,
                -3.40105048324989,
                -3.4064610296928,
                -3.40540986357853,
                 -3.40822032632123,
                -3.38885965373524,
                -3.40105048324989,
                -3.4064610296928,
                -3.40540986357853,
                -3.4064610296928,
                -3.40540986357853


        };


        for (int i = 0; i < 10; i++) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(pointX[i], pointY[i])));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(pointX[i], pointY[i])));

        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8642, -4.2518), 13));





    }

            }





