package com.example.trafficscotland;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class roadworks extends AppCompatActivity
{
    private TextView rawDataDisplay;
    private String result;
    private String trimmed;
    private String removedrss;
    Button button;


    private Button startButton;

    // Traffic Scotland URLs
    private String urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    //private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadworks);
        Button btn1 = (Button) findViewById(R.id.map);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(roadworks.this, roadmap.class);
                roadworks.this.startActivity(myIntent);
            }
        });

        startProgress();



    }


    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

                      URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //




                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    trimmed = result.replace("null", "");
                    removedrss = trimmed.replace("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>", "");





                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            roadworks.this.runOnUiThread(new Runnable()
            {

                public void parserrunner(){
                    try {
                        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
                        HashMap<String,String> item = new HashMap<>();
                        ListView lv = findViewById(R.id.listView);
                        InputStream inputStream =getAssets().open("roadworks.xml");
                        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserFactory.newPullParser();
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                        //in order to read from the file use parser.setInput(inputsteram , null);

                        parser.setInput(new StringReader(removedrss));
                        String tag = "" , text = "";
                        int event = parser.getEventType();
                        while (event!= XmlPullParser.END_DOCUMENT){
                            tag = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    if(tag.equals("item"))
                                        item = new HashMap<>();
                                    break;
                                case XmlPullParser.TEXT:
                                    text=parser.getText();
                                    break;
                                case XmlPullParser.END_TAG:
                                    switch (tag) {
                                        case "title":
                                            String info = "title";
                                            item.put(info,text);
                                            break;
                                        case "description":
                                            String name = "description";
                                            String newname = name.replaceAll("<br />", "$1F$2");
                                            item.put(newname,text);
                                            break;

                                        case "georss:point":
                                            item.put("georss:point", text);
                                            break;
                                        case "pubDate":


                                            item.put("pubDate",text);
                                            break;
                                        case "item":
                                            if(item!=null)
                                                userList.add(item);
                                            break;
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                        ListAdapter adapter = new SimpleAdapter(roadworks.this, userList, R.layout.row,
                                new String[]{"title","description","pubDate"}, new int[]{R.id.tvName,
                                R.id.tvDesignation, R.id.tvLocation, });
                        lv.setAdapter(adapter);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }

                }
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    String data = removedrss;
                    //here is going to be the pull parser inserted
                    parserrunner();


                }

            });


        }

    }


} // End of MainActivity