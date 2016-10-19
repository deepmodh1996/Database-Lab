


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;





public class homeScreenActivity extends Activity implements
        OnClickListener {
    Button addCoursesButton;
    Button logoutButton;
    TextView outputText;
    ListView lv;

    ArrayList<String> stringArray = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    String id;
    String URL;
    String course_id = "";
    String sec_id = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
        URL = sharedpreferences.getString("URL",null);
        if(URL == null || URL.equals("null")){
            Intent intent = new Intent(this, InsertUrlActivity.class);
            startActivity(intent);
        }
        id = sharedpreferences.getString("ID",null);
        if(id == null || id.equals("null")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


        addCoursesButton = (Button) findViewById(R.id.addCoursesButtonHomeScreen);
        logoutButton = (Button) findViewById(R.id.logoutButtonHomeScreen);
        outputText = (TextView) findViewById(R.id.outputHomeScreen);
        lv = (ListView) findViewById(R.id.listViewHomeScreen);

        final GetXMLTask task = new GetXMLTask();
        task.execute(new String[] { URL+"/register" });

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                stringArray );


        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                final String[] sp = stringArray.get(position).split(",");
                AlertDialog alertDialog = new AlertDialog.Builder(homeScreenActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Do you want to drop this course?\n"+sp[2]);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                outputText.setText("ok to drop course\n");
                                stringArray.clear();

                                course_id = sp[0];
                                sec_id = sp[1];
                                GetXMLTask task2 = new GetXMLTask();
                                task2.execute(new String[] { URL+"/AddDelete" });

                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                outputText.setText("does not want to drop it");
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        addCoursesButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.addCoursesButtonHomeScreen:
                Intent intent = new Intent(this, AddCourses.class);
                startActivity(intent);
                break;

            case R.id.logoutButtonHomeScreen:
                SharedPreferences sharedpreferences = getSharedPreferences(InsertUrlActivity.sessionPreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                editor.putString("URL",URL);
                editor.commit();

                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
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
                    String dataString = "delete,"+course_id+","+sec_id.substring(1);
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
                JSONArray jsonArray = new JSONArray(jsonArrayString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonElement = new JSONObject(jsonArray.getString(i));
                    String jsonElementString = jsonElement.getString("course_id") + ", "
                            + jsonElement.getString("sec_id") + ", "
                            + jsonElement.getString("title") + ", "
                            + jsonElement.getString("dept_name") + ", "
                            + jsonElement.getString("credits") +", X ";
                    stringArray.add(jsonElementString);
                }

                if(json.getString("status").equals("false")){
                    outputText.setText(json.getString("message"));
                }

                arrayAdapter.notifyDataSetChanged();

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

