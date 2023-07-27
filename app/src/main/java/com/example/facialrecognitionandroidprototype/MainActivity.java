package com.example.facialrecognitionandroidprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text = findViewById(R.id.text_view);
        text.setText("Please ensure you are constantly connected to the internet for good user experience with this mobile application.");
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        boolean haveInternet = isInternetAvailable(this);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInternetAvailable(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "You are not connected to internet. Please connect to internet to proceed with the activities in this mobile application. ", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private final boolean isInternetAvailable(Context context) {
        boolean result = false;
        Object connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.net.ConnectivityManager");
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) connManager;
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }

            Intrinsics.checkNotNullExpressionValue(network, "connectivityManager.activeNetwork ?: return false");
            Network networkCapabilities = network;
            NetworkCapabilities capability = connectivityManager.getNetworkCapabilities(networkCapabilities);
            if (capability == null) {
                return false;
            }

            Intrinsics.checkNotNullExpressionValue(capability, "connectivityManager.networkCapabilities ?: return false");
            NetworkCapabilities actNw = capability;
            result = actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ? true : (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ? true : actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            return result;
        }

    }
}