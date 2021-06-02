package com.example.hearing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    Button dialog,modulate,tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main2 );

        dialog=(Button)findViewById ( R.id.dialog );
        modulate=(Button)findViewById ( R.id.modulate );
        tts=(Button)findViewById ( R.id.tts );
        dialog.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext (),dialogActivity.class);
                startActivity (intent);
            }
        } );
        modulate.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext (),ModulateActivity.class);
                startActivity ( intent );
            }
        } );
        tts.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext (),tts.class);
                startActivity ( intent );
            }
        } );
    }

}
