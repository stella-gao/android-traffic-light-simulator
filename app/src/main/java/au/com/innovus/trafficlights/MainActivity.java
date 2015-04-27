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
    int timeRed, timeYellow, timeGreen;
    private ImageView imageArrow;
    private RunSimlulatorTask simlulatorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // ((GradientDrawable) (findViewById(R.id.green_view).getBackground())).setColor(Color.GREEN);
       // ((GradientDrawable) (findViewById(R.id.yellow_view).getBackground())).setColor(Color.YELLOW);
        imageArrow = (ImageView) findViewById(R.id.image_arrow);
        seekBars = new SeekBar[]{(SeekBar) findViewById(R.id.seekBar_red),
                (SeekBar) findViewById(R.id.seekBar_yellow),
                (SeekBar) findViewById(R.id.seekBar_green)
        };

        for (int i =0; i < seekBars.length; i++){
            seekBars[i].setMax(10);
            seekBars[i].setOnSeekBarChangeListener(this);
        }
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
        if (seekBar.getId() == findViewById(R.id.seekBar_red).getId()){
            timeRed = progress;
        }else if(seekBar.getId() == findViewById(R.id.seekBar_yellow).getId()){
            timeYellow = progress;
        }else{
            timeGreen = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == findViewById(R.id.seekBar_red).getId()){
            Log.d(TAG, "RED");
        }else if(seekBar.getId() == findViewById(R.id.seekBar_yellow).getId()){
            Log.d(TAG, "YELLOW");
        }else{
            Log.d(TAG, "GREEN");
        }
    }

    public void onClick(View v){
        if (v.getId() == R.id.button_start){
            Log.d(TAG, "start");
            startSimulation();
        }else{
            Log.d(TAG, "Stop");
            stopSimulation();
        }
    }

    private void stopSimulation(){

        simlulatorTask.cancel(true);
        timeGreen = timeYellow = timeRed = 0;
        for (SeekBar s : seekBars){
            s.setEnabled(true);
            s.setProgress(0);
        }
        findViewById(R.id.button_start).setEnabled(true);
        findViewById(R.id.button_stop).setEnabled(false);

    }
    private void startSimulation(){

        int total = timeGreen + timeRed + timeYellow;

        if (total == 0) {
            Toast.makeText(this, "Select duration", Toast.LENGTH_SHORT).show();
        }

        for (SeekBar s : seekBars){
            s.setEnabled(false);
        }
        findViewById(R.id.button_start).setEnabled(false);
        findViewById(R.id.button_stop).setEnabled(true);


        ((TextView) (findViewById(R.id.textView_timer))).setText(""+timeGreen);
        Log.d(TAG, "total  "+ total);

        simlulatorTask = new RunSimlulatorTask();
        simlulatorTask.execute(total, timeGreen, timeYellow, timeRed);

    }

    public class RunSimlulatorTask extends AsyncTask<Integer, Integer, Void>{

        @Override
        protected Void doInBackground(Integer... params) {

            int total = params[0];
            int tGreen = params[1];
            int tYellow = params[2];
            int tRed = params[3];
            while (total > 0 && !isCancelled()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                total--;

                if (total >= tYellow + tRed){
                    tGreen--;
                    publishProgress(tGreen, 0);
                } else if (total >= tRed && total < tYellow + tRed){
                    tYellow--;
                    publishProgress(tYellow, 1);
                }else{
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
            ((TextView) (findViewById(R.id.textView_timer))).setText(""+values[0]);

            switch (current){
                //Green
                case 0:
                    imageArrow.setImageResource(R.mipmap.green_arrow);
                    break;
                //Yellow
                case 1:
                    imageArrow.setImageResource(R.mipmap.yellow_arrow);
                    break;
                case 2:
                    imageArrow.setImageResource(R.mipmap.red_arrow);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopSimulation();
        }
    }
}
