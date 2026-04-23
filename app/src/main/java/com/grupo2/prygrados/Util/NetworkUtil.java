package com.grupo2.prygrados.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean hayInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork =
                cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnected();
    }
}