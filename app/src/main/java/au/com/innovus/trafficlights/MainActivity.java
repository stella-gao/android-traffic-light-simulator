package au.com.innovus.trafficlights;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar[] seekBars;
    private static String TAG = MainActivity.class.getSimpleName();
    int timeRed = -1, timeYellow = -1 , timeGreen = -1;
    private ImageView imageArrow;
    private RunSimlulatorTask simlulatorTask;
    private boolean running = false;
    //current 0 green, 1 yellow, 2, red
    private int currentColor = 0;
    private boolean resumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeRed = timeYellow = timeGreen = -1;
        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        seekBars = new SeekBar[]{(SeekBar) findViewById(R.id.seekBar_red),
                (SeekBar) findViewById(R.id.seekBar_yellow),
                (SeekBar) findViewById(R.id.seekBar_green)
        };

        for (int i = 0; i < seekBars.length; i++) {
            seekBars[i].setMax(10);
            seekBars[i].setOnSeekBarChangeListener(this);
        }

        if (savedInstanceState != null) {
            boolean run = savedInstanceState.getBoolean("running", false);
            if (run) {
                resumed = true;
                Log.d(TAG, "onCreate()");
                timeGreen = savedInstanceState.getInt("time_green");
                timeRed = savedInstanceState.getInt("time_red");
                timeYellow = savedInstanceState.getInt("time_yellow");
                currentColor = savedInstanceState.getInt("current");
                running = false;
                startSimulation();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() " + running + " timeRed " +timeRed+
                " timeYellow "+ timeYellow+
                " timeGreen "+ timeGreen);
        startSimulation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == findViewById(R.id.seekBar_red).getId()) {
            Log.d(TAG, "RED");
        } else if (seekBar.getId() == findViewById(R.id.seekBar_yellow).getId()) {
            Log.d(TAG, "YELLOW");
        } else {
            Log.d(TAG, "GREEN");
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_start) {
            Log.d(TAG, "ButtonStart");
            startSimulation();
        } else {
            Log.d(TAG, "ButtonStop");
            stopSimulation();
        }
    }

    private void stopSimulation() {

        running = false;
        simlulatorTask.cancel(true);
        for (SeekBar s : seekBars) {
            s.setEnabled(true);
            s.setProgress(0);
        }

        timeRed = timeYellow = timeGreen = -1;
        findViewById(R.id.button_start).setEnabled(true);
        findViewById(R.id.button_stop).setEnabled(false);
        imageArrow.setVisibility(View.INVISIBLE);
        ((TextView) (findViewById(R.id.textView_timer))).setText("");

        ((GradientDrawable) (findViewById(R.id.green_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
        ((GradientDrawable) (findViewById(R.id.yellow_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
        ((GradientDrawable) (findViewById(R.id.red_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));

    }

    private void startSimulation() {

        if (timeRed < 0) timeRed = seekBars[0].getProgress();
        if (timeYellow < 0) timeYellow = seekBars[1].getProgress();
        if (timeGreen < 0) timeGreen = seekBars[2].getProgress();

        if (timeGreen > 0) currentColor = 0;

        if (timeGreen == 0 && timeYellow > 0){
            currentColor = 1;
        }if (timeGreen == 0 && timeYellow == 0){
            currentColor = 2;
        }
        running = true;
        Log.d(TAG, "StartSimulation Current Color " + currentColor);
        int total = timeGreen + timeRed + timeYellow;

        if (total == 0) {
            Toast.makeText(this, "Select duration", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentTime = 0;
        int currentArrow =0;
        String colorGreen = "", colorYellow = "", colorRed = "";
        //red
        if (currentColor == 0){
            currentTime = timeGreen;
            colorGreen = "#4CAF50";
            colorYellow = "#000000";
            colorRed = "#000000";
            currentArrow = R.mipmap.green_arrow;

        //yellow
        }else if (currentColor == 1){
            currentTime = timeYellow;
            colorGreen = "#000000";
            colorYellow = "#FFEB3B";
            colorRed = "#000000";
            currentArrow = R.mipmap.yellow_arrow;
        //red
        }else{
            currentTime = timeRed;
            colorGreen = "#000000";
            colorYellow = "#000000";
            colorRed = "#FFFF0000";
            currentArrow = R.mipmap.red_arrow;
        }

        ((GradientDrawable) (findViewById(R.id.green_view_traffic).getBackground())).setColor(Color.parseColor(colorGreen));
        ((GradientDrawable) (findViewById(R.id.yellow_view_traffic).getBackground())).setColor(Color.parseColor(colorYellow));
        ((GradientDrawable) (findViewById(R.id.red_view_traffic).getBackground())).setColor(Color.parseColor(colorRed));
        ((TextView) (findViewById(R.id.textView_timer))).setText("" + currentTime);

        imageArrow.setImageResource(currentArrow);
        imageArrow.setVisibility(View.VISIBLE);
        for (SeekBar s : seekBars) {
            s.setEnabled(false);
        }
        findViewById(R.id.button_start).setEnabled(false);
        findViewById(R.id.button_stop).setEnabled(true);

        ((TextView) (findViewById(R.id.textView_timer))).setVisibility(View.VISIBLE);

        Log.d(TAG, "total  " + total);

        simlulatorTask = new RunSimlulatorTask();
        simlulatorTask.execute(total, timeGreen, timeYellow, timeRed);
    }

    public class RunSimlulatorTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            int total = params[0];
            int tGreen = params[1];
            int tYellow = params[2];
            int tRed = params[3];
            while (total > 0 && !isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                total--;
                //Log.d(TAG, "BACKGORUND");

                if (total >= tYellow + tRed) {
                    tGreen--;
                    publishProgress(tGreen, 0);
                } else if (total >= tRed && total < tYellow + tRed) {
                    tYellow--;
                    publishProgress(tYellow, 1);
                } else {
                    tRed--;
                    publishProgress(tRed, 2);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int current = values[1];

            ((TextView) (findViewById(R.id.textView_timer))).setText("" + values[0]);

            switch (current) {
                //Green
                case 0:
                    timeGreen = values[0];
                    imageArrow.setImageResource(R.mipmap.green_arrow);
                    currentColor = 0;
                    //((GradientDrawable) (findViewById(R.id.green_view).getBackground())).setColor(Color.parseColor("#4CAF50"));
                    ((GradientDrawable) (findViewById(R.id.green_view_traffic).getBackground())).setColor(Color.parseColor("#4CAF50"));
                    //((GradientDrawable) (findViewById(R.id.yellow_view).getBackground())).setColor(Color.parseColor("#000000"));
                    //((GradientDrawable) (findViewById(R.id.red_view).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.yellow_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.red_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    break;
                //Yellow
                case 1:
                    timeYellow = values[0];
                    imageArrow.setImageResource(R.mipmap.yellow_arrow);
                    currentColor =1;
                    //((GradientDrawable) (findViewById(R.id.yellow_view).getBackground())).setColor(Color.parseColor("#FFEB3B"));
                    ((GradientDrawable) (findViewById(R.id.yellow_view_traffic).getBackground())).setColor(Color.parseColor("#FFEB3B"));
                    //((GradientDrawable) (findViewById(R.id.green_view).getBackground())).setColor(Color.parseColor("#000000"));
                    //((GradientDrawable) (findViewById(R.id.red_view).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.green_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.red_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    break;
                case 2:
                    timeRed = values[0];
                    imageArrow.setImageResource(R.mipmap.red_arrow);
                    currentColor = 2;
                    //((GradientDrawable) (findViewById(R.id.red_view).getBackground())).setColor(Color.parseColor("#FFFF0000"));
                    ((GradientDrawable) (findViewById(R.id.red_view_traffic).getBackground())).setColor(Color.parseColor("#FFFF0000"));
                    //((GradientDrawable) (findViewById(R.id.green_view).getBackground())).setColor(Color.parseColor("#000000"));
                    //((GradientDrawable) (findViewById(R.id.yellow_view).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.green_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    ((GradientDrawable) (findViewById(R.id.yellow_view_traffic).getBackground())).setColor(Color.parseColor("#000000"));
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPosteExecute()");
            stopSimulation();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (simlulatorTask != null && !simlulatorTask.isCancelled()) {
            Log.d(TAG, "onStop()");
            simlulatorTask.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (running) {
            outState.putInt("time_red", timeRed);
            outState.putInt("time_yellow", timeYellow);
            outState.putInt("time_green", timeGreen);
            outState.putBoolean("running", running);
            outState.putInt("current", currentColor);
            Log.d(TAG, "onSavedInstance() " + running + " timeRed " +timeRed+
            " timeYellow "+ timeYellow+
            " timeGreen "+ timeGreen);
        }
        super.onSaveInstanceState(outState);
    }
}