package com.dot.blockchainapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InspectionActivity extends AppCompatActivity {
    private static EditText editText;
    private static final int REQUEST_TRUE = 777;
    private static final int REQUEST_FALSE = 666;

    private static final int VERIFY_TRUE = 7777;
    private static final int VERIFY_FALSE = 6666;

    private static final String TRUE_RESPONSE = "Hello nodejs;";
    private static final String FALSE_RESPONSE = "false";
    private static final String FAIL_RESPONSE = "fail";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        editText = this.findViewById(R.id.editText);

        Intent intent = getIntent();
        String text = intent.getExtras().getString("text");
        editText.setText(text);
    }

    static JSONObject text2Json() {
        JSONObject jsonObject = new JSONObject();
        String[] block_list = editText.getText().toString().split("\n\n");
        JSONArray contents = new JSONArray();
        for (String block_str: block_list) {
            JSONArray block = new JSONArray();

            String[] line_list = block_str.split("\n");
            try {
                for (String line_str : line_list) {
                    JSONObject line = new JSONObject();
                    line.put("line", line_str);
                    block.put(line);
                }
                JSONObject block_obj = new JSONObject();
                block_obj.put("block", block);
                contents.put(block_obj);
                
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        try {
            jsonObject.put("content", contents);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = gson.toJson(jsonObject);

        Log.e("InspectionActivity", prettyJsonString);


        return jsonObject;
    }

    static String requestServer(JSONObject jsonObject) {
        try {
            String response = new SendDeviceDetails().execute("http://192.249.31.196:8888/", jsonObject.toString()).get();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
    }

    public void returnToMainActivity() {
        Intent mainIntent = new Intent(InspectionActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


    public void showAlert(String title, String message, int requestCode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Go home",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Go home",Toast.LENGTH_SHORT).show();
                        returnToMainActivity();
                    }
                });
        if (requestCode == REQUEST_FALSE || requestCode == VERIFY_FALSE) {
            builder.setNegativeButton("Try again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        builder.show();
    }

    public void registerButton(View v){
        JSONObject jsonObject = text2Json();

        String response = requestServer(jsonObject);

        if (response.equals(TRUE_RESPONSE)) {
            showAlert("Request True", "Congratulations!", REQUEST_TRUE);
        } else if (response.equals(FALSE_RESPONSE)) {
            showAlert("Request False", "Rejected register.", REQUEST_FALSE);
        } else if (response.equals(FAIL_RESPONSE)) {
            showAlert("Request Fail", "Network connection fail.", REQUEST_FALSE);
        } else {
            showAlert("Request Exception", "Exception occurs.", REQUEST_FALSE);
        }

    }

    public void verifyButton(View v){
        JSONObject jsonObject = text2Json();

        String response = requestServer(jsonObject);

        if (response.equals(TRUE_RESPONSE)) {
            showAlert("Verify True", "Verified document!", VERIFY_TRUE);
        } else if (response.equals(FALSE_RESPONSE)) {
            showAlert("Verify False", "Unverified document.", VERIFY_FALSE);
        } else if (response.equals(FAIL_RESPONSE)) {
            showAlert("Verify Fail", "Network connection fail.", VERIFY_FALSE);
        } else {
            showAlert("Verify Exception", "Exception occurs.", VERIFY_FALSE);
        }

    }


    private static class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            editText.setText(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
