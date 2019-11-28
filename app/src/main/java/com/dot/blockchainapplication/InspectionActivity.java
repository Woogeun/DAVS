package com.dot.blockchainapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class InspectionActivity extends AppCompatActivity {
    private static EditText editText;
    private static final int REQUEST_SUCCESS = 777;
    private static final int REQUEST_FAIL_REJECTED = 666;
    private static final int REQUEST_FAIL_NETWORK = 444;

    private static final int VERIFY_SUCCESS = 7777;
    private static final int VERIFY_FAILURE = 6666;


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

        for (String text: text_list) {

        }

        try {
            jsonObject.put("content", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    static Boolean requestServer(JSONObject jsonObject) {

        return true;
    }

    public void returnToMainActivity() {
        Intent mainIntent = new Intent(InspectionActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


    void showAlert(String title, String message, int requestCode)
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
}
