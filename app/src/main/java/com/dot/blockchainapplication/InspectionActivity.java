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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InspectionActivity extends AppCompatActivity {
    private static EditText editText;
    private static final int REQUEST_SUCCESS = 777;
    private static final int REQUEST_FAIL_REJECTED = 666;
    private static final int REQUEST_FAIL_NETWORK = 444;

    private static final int VERIFY_SUCCESS = 7777;
    private static final int VERIFY_FAILURE = 6666;

    private static String responseServer = null;
    private static final String SUCCESS_RESPONSE = "Hello nodejs";
//    private static final String SUCCESS_RESPONSE = "Success";
    private static final String FAILURE_RESPONSE = "Failure";


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
        String[] text_list = editText.getText().toString().split("\n");

        try {
            jsonObject.put("content", text_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    static class FoolException extends RuntimeException {

    }

    static Boolean requestServer(JSONObject jsonObject) {
        new SendDeviceDetails().execute("http://110.76.82.39:8888/", jsonObject.toString());

        if (responseServer == SUCCESS_RESPONSE) {
            return true;
        } else if (responseServer == FAILURE_RESPONSE) {
            return false;
        } else {
            throw new FoolException();
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
        if (requestCode == REQUEST_FAIL_NETWORK) {
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

        try {
            Boolean response = requestServer(jsonObject);

            if (response) {
                showAlert("Request Success", "Congratulations!", REQUEST_SUCCESS);
            } else {
                showAlert("Request Failure", "Rejected register.", REQUEST_FAIL_REJECTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Request Failure", "Network connection Failed.", REQUEST_FAIL_NETWORK);
        }
    }

    public void verifyButton(View v){
        JSONObject jsonObject = text2Json();

        try {
            Boolean response = requestServer(jsonObject);

            if (response) {
                showAlert("Verify Success", "Verified document", VERIFY_SUCCESS);
            } else {
                showAlert("Verify Failure", "Unverified document", VERIFY_FAILURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Request Failure", "Network connection Failed.", REQUEST_FAIL_NETWORK);
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
            responseServer = result;
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
