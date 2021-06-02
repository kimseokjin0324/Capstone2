package com.example.hearing;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.resample.RateTransposer;

public class ModulateActivity extends AppCompatActivity {
    public static Listen listening;
    AudioDispatcher dispatcher;
    AudioProcessor pitchProcessor;
    AndroidAudioPlayer player;
    Button on_off;
    RateTransposer rateTransposer;

    public static boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulate);

        on_off=(Button)findViewById ( R.id.on_off );

        listening=new Listen ();
        listening.execute();

        on_off.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(flag){//flag->true일때->false바꿔줌 시작->종료
                    flag=false;
                    on_off.setText("종료");
                    listening=new Listen ();
                    listening.execute (  );
                }
                else {//flag:flase->true(종료->시작)
                    flag=true;
                    on_off.setText ( "시작" );

                }
            }
        } );

    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class Listen extends AsyncTask<Void,double[],Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            final int bufferSize= AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT);
            dispatcher= AudioDispatcherFactory.fromDefaultMicrophone(44100,bufferSize,bufferSize/2);//마이크로 부터 음성을 받아 오는 부분
            player=new AndroidAudioPlayer ( dispatcher.getFormat ());
            double factor=centToFactor(0);//음성변조하는 부분
            PitchDetectionHandler pitch =new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                    final float pitch=pitchDetectionResult.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!flag) {
                                if(pitch>=dialogActivity.codes[9][0]&&pitch<dialogActivity.codes[9][1])
                                 modulate ( -100 );//한음이 낮아짐
                                else
                                    modulate(0);
                            }
                            else
                              Listen.super.cancel ( true );
                            dispatcher.stop();
                        }

                    });//지속적으로 음성의 음계를 분석하는 부분
                }

            };
            pitchProcessor=new PitchProcessor (PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,44100,bufferSize,pitch);
            rateTransposer=new RateTransposer (factor);

            dispatcher.addAudioProcessor(pitchProcessor);
            dispatcher.addAudioProcessor (rateTransposer);
            dispatcher.addAudioProcessor (player);
            dispatcher.run();
            return null;
        }
    }
    private void modulate(int cents)
    {
        double factor=centToFactor ( cents );
        rateTransposer.setFactor(factor);
    }
    public static double centToFactor(double cents){
        return 1/Math.pow(Math.E,cents*Math.log(2)/1200/Math.log(Math.E));
    }
}


