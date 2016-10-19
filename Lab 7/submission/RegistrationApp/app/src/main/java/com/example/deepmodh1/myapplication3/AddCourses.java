


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
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;





public class AddCourses extends Activity implements
        OnClickListener {
    Button addCourseButton;
    Button backButton;
    TextView outputText;
    AutoCompleteTextView autoCompleteText;

    ArrayList<String> stringArray = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    String URL;
    String course_id = "";
    String sec_id = "";
    String id;
    String state = "onCreate"; // state stores which url is being called ( used in onPostExecute )

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);

        SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
        URL = sharedpreferences.getString("URL",null);
        if(URL == null || URL.equals("null")){
            Intent intent = new Intent(this, InsertUrlActivity.class);
            startActivity(intent);
        }
        id = sharedpreferences.getString("ID",null);
        if(id == null || id.equals("null")) {
            Intent intent4 = new Intent(this, LoginActivity.class);
            startActivity(intent4);
        }


        addCourseButton = (Button) findViewById(R.id.buttonAddCourses);
        backButton = (Button) findViewById(R.id.backButtonAddCourses);
        outputText = (TextView) findViewById(R.id.outputAddCourses);
        autoCompleteText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewAddCourses);

        final GetXMLTask task = new GetXMLTask();
        task.execute(new String[] { URL+"/courses" });

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                stringArray );

        autoCompleteText.setAdapter(arrayAdapter);
        autoCompleteText.setThreshold(1);

        addCourseButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddCourses:
                state = "addCourse";
                stringArray.clear();
                String[] sp = autoCompleteText.getText().toString().split(",");
                if(sp.length<2){
                    course_id = "#####";
                    sec_id = "#####";
                }
                else {
                    course_id = sp[0];
                    sec_id = sp[1];
                }

                GetXMLTask task2 = new GetXMLTask();
                task2.execute(new String[]{URL + "/AddDelete"});

                autoCompleteText.setText("");
                break;

            case R.id.backButtonAddCourses:
                goToHomeScreen();
                break;
            default:
                break;
        }
    }

    public void printAlert(String status, String textToPrint){
        AlertDialog alertDialog = new AlertDialog.Builder(AddCourses.this).create();
        alertDialog.setTitle("Alert");
        if(status.equals("true")) {
            alertDialog.setMessage("Status : " + status);
        }
        else{
            alertDialog.setMessage("Status : "+status+"\nMessage : "+textToPrint);
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                       goToHomeScreen();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public void goToHomeScreen() {
        Intent intent2 = new Intent(this, homeScreenActivity.class);
        startActivity(intent2);
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
                postDataParams.put("id",id);

                if(urlString.equals(URL+"/AddDelete")){
                    String dataString = "add,"+course_id+","+sec_id.substring(1);
                    postDataParams.put("data",dataString);
                }

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
                String jsonArrayString = json.getString("data");

                String status = json.getString("status");
                if(status.equals("true")){

                    switch (state) {
                        case "onCreate":
                            JSONArray jsonArray = new JSONArray(jsonArrayString);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonElement = new JSONObject(jsonArray.getString(i));
                                String jsonElementString = jsonElement.getString("course_id") + ", "
                                        + jsonElement.getString("sec_id") + ", "
                                        + jsonElement.getString("title") + ", "
                                        + jsonElement.getString("dept_name") + ", "
                                        + jsonElement.getString("credits");
                                stringArray.add(jsonElementString);
                            }

                            arrayAdapter.notifyDataSetChanged();
                            break;
                        case "addCourse":
                            printAlert(json.getString("status"),json.getString("data"));
                            break;
                        default:
                            break;
                    }

                }
                else{

                    switch (state) {
                        case "onCreate":
                            outputText.setText(json.getString("message"));
                            break;
                        case "addCourse":
                            printAlert(json.getString("status"),json.getString("message"));
                            break;
                        default:
                            break;
                    }

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

