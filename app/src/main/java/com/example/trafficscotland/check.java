package com.example.trafficscotland;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficscotland.R;

public class check extends AppCompatActivity
{

    private Button startButton;
    private ProgressBar progressBar;
    private TextView txt_percentage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        startButton = (Button) findViewById(R.id.btn_start);
        progressBar =  (ProgressBar) findViewById(R.id.progress);
        txt_percentage= (TextView) findViewById(R.id.txt_percentage);


        startButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        new ShowDialogAsyncTask().execute();
    }


    private class ShowDialogAsyncTask extends AsyncTask<Void, Integer, Void>
    {

        int progress_status;

        @Override
        protected void onPreExecute()
        {
            // update the UI immediately after the task is executed
            super.onPreExecute();

            Toast.makeText(check.this,"Invoke onPreExecute()", Toast.LENGTH_SHORT).show();

            progress_status = 0;
            txt_percentage.setText("Fetching Data  0%");

        }

        @Override
        protected Void doInBackground(Void... params)
        {

            while(progress_status<100)
            {

                progress_status += 2;

                publishProgress(progress_status);
                // Sleep but normally do something useful here such as access a web resource
                SystemClock.sleep(40); // or Thread.sleep(300);

                // Really need to do some calculation on progress
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);

            txt_percentage.setText("Downloading " +values[0] + "%");

        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            Toast.makeText(check.this,
                    "Invoke onPostExecute()", Toast.LENGTH_SHORT).show();

            txt_percentage.setText("");
            startButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);

        }

    }

}