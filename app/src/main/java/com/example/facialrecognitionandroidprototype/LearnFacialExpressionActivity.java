package com.example.facialrecognitionandroidprototype;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LearnFacialExpressionActivity extends AppCompatActivity {
    private final String[] permissions = {Manifest.permission.CAMERA};
    private final int REQ_CAMERA = 1;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    private PreviewView previewView;
    private Button takepic, returnbtn;
    private ImageView imgview;
    private WebView webView;
    private TextView text;

    private int rotation = 0;

    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_facial_expression);

        getPermission();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        previewView = findViewById(R.id.camera);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                showErrorDialog();
            }
        }, getExecutor());

        text = findViewById(R.id.text);
        takepic = findViewById(R.id.takepic);
        takepic.setOnClickListener(view -> takePicture());
        returnbtn = findViewById(R.id.returnbtn);
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchHome = new Intent(LearnFacialExpressionActivity.this, MenuActivity.class);
                startActivity(launchHome);
                finish();
            }
        });

        imgview = findViewById(R.id.image);
        webView = findViewById(R.id.WebView);

        OrientationEventListener orientationEventListener = new OrientationEventListener(this)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                rotation = orientation;
            }
        };

        orientationEventListener.enable();
    }

    @Override
    protected void onResume(){
        super.onResume();
        monitorConnection();
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private ConnectivityManager.NetworkCallback connectionCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network){
            runOnUiThread(() -> {
                takepic.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public void onLost(Network network) {
            Network currentNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(currentNetwork);
            
            if(capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)){
                return;
            }

            runOnUiThread(() -> {
                setInternetText();
                takepic.setVisibility(View.INVISIBLE);
                text.setVisibility(View.VISIBLE);
            });
        }
    };

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void takePicture(){
        text.setVisibility(View.VISIBLE);
        takepic.setVisibility(View.INVISIBLE);
        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCaptureSuccess (ImageProxy imageProxy) {
                int fixer;
                if (rotation >= 45 && rotation < 135) {
                    fixer = 180;
                } else if (rotation >= 135 && rotation < 225) {
                    fixer = 90;
                } else if (rotation >= 225 && rotation < 315) {
                    fixer = 0;
                } else {
                    fixer = 270;
                }
                processImage(imageProxy.getImage(), (float) fixer);
                imageProxy.close();
            }

            @Override
            public void onError (ImageCaptureException exception) {
                showErrorDialog();
            }
        });
            webView.loadUrl(getResources().getString(R.string.HOST_ADDRESS) + "blank");

    }

    private void getPermission(){
        if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQ_CAMERA);
        }
    }

    private void processImage(Image image, float degrees){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        matrix.postScale(-1, 1);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        rotated.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        imgview.setImageBitmap(rotated);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        sendImage(byteArray);
    }

    @SuppressLint("RestrictedApi")
    private void sendImage(byte[] byteArray) {
        if (!checkConnection()) {
            return;
        }

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String payload = "imageBytes=" + encoded;
        Log.i("byteArray======", encoded);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                takepic.setVisibility(View.VISIBLE);

            }
        });
        webView.postUrl(getResources().getString(R.string.HOST_ADDRESS) + "android", payload.getBytes());


    }

    private void showErrorDialog(){
        new AlertDialog.Builder(LearnFacialExpressionActivity.this)
                .setTitle("Error")
                .setMessage(getString(R.string.GENERIC_ERR))
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    Intent launchHome = new Intent(this, MenuActivity.class);
                    startActivity(launchHome);
                    finish();
                })
                .show();
    }

    private void monitorConnection(){
        checkConnection();

        connectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .build(), connectionCallback);
    }

    private boolean checkConnection(){
        Network currentNetwork = connectivityManager.getActiveNetwork();
        if(currentNetwork == null){
            runOnUiThread(() -> {
                setInternetText();
                takepic.setVisibility(View.INVISIBLE);
            });
            return false;
        }
        return true;
    }

    private void setInternetText(){
        SpannableString clickable = new SpannableString(getString(R.string.NO_INTERNET_ERR));
        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View textView)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
            @Override
            public void updateDrawState(TextPaint ds)
            {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.BLUE);
            }
        };
        clickable.setSpan(clickableSpan, 44, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(clickable);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setHighlightColor(Color.TRANSPARENT);
    }
}