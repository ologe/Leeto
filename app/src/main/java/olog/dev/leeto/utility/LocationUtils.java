package olog.dev.leeto.utility;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtils {

    public static boolean isLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return isGpsEnabled && isNetworkEnabled;
    }

}
