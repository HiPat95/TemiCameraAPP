package com.example.traking_temi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.robotemi.sdk.NlpResult;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage;
import com.robotemi.sdk.listeners.OnBeWithMeStatusChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        Robot.NlpListener,
        OnRobotReadyListener,
        Robot.ConversationViewAttachesListener,
        Robot.WakeupWordListener,
        Robot.ActivityStreamPublishListener,
        Robot.TtsListener,
        OnBeWithMeStatusChangedListener,
        OnGoToLocationStatusChangedListener,
        OnLocationsUpdatedListener {

    Robot robot = Robot.getInstance();

    private ImageView addImageFace;
    private Bitmap bitmap;

    int CAMERA_PICTURE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        robot.hideTopBar();
        robot.tiltAngle(0);

        addImageFace = findViewById(R.id.addImageFace);
        addImageFace.setOnClickListener(v -> onButtonImageClicked1());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Robot.getInstance().addOnRobotReadyListener(this);
        Robot.getInstance().addNlpListener(this);
        Robot.getInstance().addOnBeWithMeStatusChangedListener(this);
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().addConversationViewAttachesListenerListener(this);
        Robot.getInstance().addWakeupWordListener(this);
        Robot.getInstance().addTtsListener(this);
        Robot.getInstance().addOnLocationsUpdatedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Robot.getInstance().removeOnRobotReadyListener(this);
        Robot.getInstance().removeNlpListener(this);
        Robot.getInstance().removeOnBeWithMeStatusChangedListener(this);
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().removeConversationViewAttachesListenerListener(this);
        Robot.getInstance().removeWakeupWordListener(this);
        Robot.getInstance().removeTtsListener(this);
        Robot.getInstance().removeOnLocationsUpdateListener(this);
    }

    @Override
    public void onPublish(@NonNull ActivityStreamPublishMessage activityStreamPublishMessage) {

    }

    @Override
    public void onConversationAttaches(boolean b) {

    }

    @Override
    public void onNlpCompleted(@NonNull NlpResult nlpResult) {

    }

    @Override
    public void onTtsStatusChanged(@NonNull TtsRequest ttsRequest) {

    }

    @Override
    public void onWakeupWord(@NonNull String s, int i) {

    }

    @Override
    public void onBeWithMeStatusChanged(@NonNull String s) {

    }

    @Override
    public void onGoToLocationStatusChanged(@NonNull String s, @NonNull String s1, int i, @NonNull String s2) {

    }

    @Override
    public void onLocationsUpdated(@NonNull List<String> list) {

    }

    @Override
    public void onRobotReady(boolean b) {
        if (robot.isReady()) {
            refreshTemiUi();
        }
    }

    private void refreshTemiUi() {
        try {
            ActivityInfo activityInfo = getPackageManager()
                    .getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Robot.getInstance().onStart(activityInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onButtonImageClicked1() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            Log.d("MainActivity", "Camera opened succesfully");
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void openCamera() {
        Intent cameraI = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraI, CAMERA_PICTURE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera permission is required to access the camera.", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    Toast.makeText(this, "Camera permission is required to access the camera.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

   @Override
   public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == CAMERA_PICTURE && resultCode == Activity.RESULT_OK) {
           if (data != null && data.getExtras() != null) {
               Bitmap capturedBitmap = (Bitmap) data.getExtras().get("data");
               if (capturedBitmap != null) {
                   // Controlling image size
                   int imageSizeInBytes = capturedBitmap.getByteCount();

                   // Checking the image size
                   int maxSizeBytes = 1024 * 1024; // 1 MB
                   if (imageSizeInBytes > maxSizeBytes) {
                       // Show an error message to the user
                       Toast.makeText(this, "Image size exceeds the maximum allowed limit.", Toast.LENGTH_SHORT).show();
                   } else {
                       // The image has been successfully captured, so assign it to "bitmap"
                       bitmap = capturedBitmap;
                       addImageFace.setImageBitmap(bitmap);
                       Log.d("MainActivity", "Image captured and set to ImageView");
                       checkImage();
                   }
               } else {
                   Toast.makeText(this, "Failed to capture image.", Toast.LENGTH_SHORT).show();
                   Log.d("MainActivity", "Failed to capture image, bitmap is null");
               }
           } else {
               Toast.makeText(this, "No image data found.", Toast.LENGTH_SHORT).show();
               Log.d("MainActivity", "No image data found in intent");
           }
       } else {
           Log.d("MainActivity", "onActivityResult: requestCode or resultCode not matching");
       }
   }

    private void checkImage(){
        // Verify that all fields have been filled in and an image has been selected
        if (bitmap == null) {
            Log.d("MainActivity", "No image captured");
            Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("MainActivity", "Image saved successfully");
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
            openCamera();
        }
    }


    private byte[] getImageDataAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public void kill_me(View view) {
        finishAffinity();
    }
}
