package com.nic.drawpoly;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class GlobalVariables {

    public final static String TargetURL = "http://eservices.bih.nic.in/rcd/mUserReport.aspx";

    public static boolean isOffline = false;
    public static boolean isOfflineGPS = false;
    public static String selectedBMType = "";
    public static String selectedCircle = "";
    public static String selectedDivision = "";
    public static String selectedCDType = "";
    public static String selectedPhotoType = "";
    public static String selectedZoneID = "";
    public static String selectedCircleID = "";
    public static String selectedDivID = "";
    public static String selectedSchemeIDADM = "";

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() == true);
    }

}
