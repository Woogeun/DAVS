package com.dot.blockchainapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InspectionActivity extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        editText = this.findViewById(R.id.editText);

        Intent intent = getIntent();
        String text = intent.getExtras().getString("text");
        editText.setText(text);
    }

    public void registerButton(View v){

    }

    public void verifyButton(View v){

    }
}
