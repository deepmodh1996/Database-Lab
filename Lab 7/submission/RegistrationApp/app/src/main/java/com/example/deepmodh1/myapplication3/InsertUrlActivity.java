

package com.example.deepmodh1.myapplication3;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class InsertUrlActivity extends Activity implements
        OnClickListener {
    Button button;
    EditText urlEditText;

    SharedPreferences sharedpreferences;

    public static final String sessionPreference = "sessionPreference" ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_url);

        button = (Button) findViewById(R.id.urlAddButton);
        urlEditText =  (EditText) findViewById(R.id.urlEdit);

        sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);

        button.setOnClickListener(this);
    }


    public void onClick(View view) {


        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("URL",urlEditText.getText().toString());
        editor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }


}