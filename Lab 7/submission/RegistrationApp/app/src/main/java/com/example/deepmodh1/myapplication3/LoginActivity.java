

package com.example.deepmodh1.myapplication3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;





public class LoginActivity extends Activity implements OnClickListener {

    Button button;
    Button insertURLbutton;

    TextView outputText;
    EditText usernameEditText;
    EditText passwordEditText;

    String username;
    String password;
    String URL;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
        URL = sharedpreferences.getString("URL",null);
        if(URL == null || URL.equals("null")) {
            Intent intent = new Intent(this, InsertUrlActivity.class);
            startActivity(intent);
        }


        button = (Button) findViewById(R.id.loginButton);
        insertURLbutton = (Button) findViewById(R.id.insertUrlButtonLogin);

        outputText = (TextView) findViewById(R.id.outputTxtMainActivity);
        usernameEditText =  (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        button.setOnClickListener(this);
        insertURLbutton.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.loginButton:
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                usernameEditText.setText("");
                passwordEditText.setText("");

                GetXMLTask task = new GetXMLTask();
                task.execute(new String[] { URL+"/login" });
                break;

            case R.id.insertUrlButtonLogin:
                SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(this, InsertUrlActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }


    }

    public void startHomeScreen(){
        SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("ID",username);
        editor.commit();

        Intent intent = new Intent(this, homeScreenActivity.class);
        startActivity(intent);
    }


    private class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader( new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }


        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("id", username);
                postDataParams.put("password", password);

                OutputStream os = httpConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
            try {
                JSONObject json = new JSONObject(output);
                String status = json.getString("status") ;
                if(status.equals("true")){
                    outputText.setText("Wait while Logging in " + json.getString("data"));
                    startHomeScreen();
                }
                else{
                    String errorMessage = json.getString("message");
                    outputText.setText(errorMessage);
                }

            }catch (Exception ex) {
                ex.printStackTrace();
            }


        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator<String> itr = params.keys();

            while(itr.hasNext()){
                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }


}