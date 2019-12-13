package com.dot.blockchainapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText.TextBlock;
import com.google.firebase.ml.vision.text.FirebaseVisionText.Line;
import com.google.firebase.ml.vision.text.FirebaseVisionText.Element;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.IOException;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    private ImageView imageView;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        imageView = this.findViewById(R.id.imageView);

        // Set image activity image to received bitmap image
        Intent imageIntent = getIntent();
        byte[] byteArray = imageIntent.getExtras().getByteArray("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bitmap);


        // Current Implementation goes through gallery pick activity.
        // Picked image is transmitted to onActivityResult method.
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }


    // Received image and do OCR
    // Structured string transmitted to InspectionActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri uri = data.getData();
            try {
                // Convert bitmap image to FireBaseVision image type for OCR
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                // Action for each success and failure of OCR
                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {

                            // Success
                            @Override
                            public void onSuccess(FirebaseVisionText result) {

                                String resultText = "";

                                for (TextBlock block: result.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();
                                    for (Line line: block.getLines()) {
                                        String lineText = line.getText();
                                        Float lineConfidence = line.getConfidence();
                                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                        Point[] lineCornerPoints = line.getCornerPoints();
                                        Rect lineFrame = line.getBoundingBox();
//                                        for (Element element: line.getElements()) {
//                                            String elementText = element.getText();
//                                            Float elementConfidence = element.getConfidence();
//                                            List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
//                                            Point[] elementCornerPoints = element.getCornerPoints();
//                                            Rect elementFrame = element.getBoundingBox();
//                                        }
                                        resultText += lineText + "\n";
                                    }

                                    resultText += "\n";
                                }

                                // Send the structured string to InspectionActivity
                                Intent inspectionIndent = new Intent(LoadingActivity.this, InspectionActivity.class);
                                inspectionIndent.putExtra("text", resultText);
                                startActivity(inspectionIndent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {

                            // Failure
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAlert("OCR Failed", "Try Other picture");

                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method for return to main activity
    public void returnToMainActivity() {
        Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    // Show alert method
    void showAlert(String title, String message)
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
    }
}

