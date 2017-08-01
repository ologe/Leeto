package olog.dev.leeto.activity_add_journey;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import olog.dev.leeto.R;
import olog.dev.leeto.utility.LocationUtils;
import olog.dev.leeto.utility.Permission;

public class AddJourneyPresenter implements AddJourneyContract.Presenter {

    private WeakReference<AddJourneyContract.View> view;

    public AddJourneyPresenter(AddJourneyContract.View view){
        this.view = new WeakReference<>(view);
    }

    @Override
    public void showDatePicker(DatePickerDialog dialog) {
        dialog.show();
    }

    @Override
    public void onLocationRequestClick(Context context, olog.dev.leeto.model.pojo.Location location) {
        if(Permission.checkLocationPermission((Activity) context)){
            getCurrentLocation(context, location);
        } else Permission.requestLocationPermission((Activity) context);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(Context context, olog.dev.leeto.model.pojo.Location locationObject){

          if(!LocationUtils.isLocationEnabled(context)){
            Toast.makeText(context, R.string.enable_location, Toast.LENGTH_SHORT).show();
        }

        LocationServices.getFusedLocationProviderClient(context)
                .getLastLocation()
                .addOnSuccessListener((Activity) context, location -> {
                    if(location == null) return;

                    locationObject.setLatitude(location.getLatitude());
                    locationObject.setLongitude(location.getLongitude());

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                    try {
                        Address address = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1).get(0);

                        locationObject.setName(address.getCountryName());
                        locationObject.setAddress(address.getThoroughfare() + ", " + address.getLocality());

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, R.string.location_not_found, Toast.LENGTH_SHORT).show();
                    }

                    if(view.get() != null){
                        view.get().updateLocation(locationObject);
                    }


                }).addOnFailureListener((Activity) context, e -> {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.location_something_went_wrong, Toast.LENGTH_SHORT).show();
                });
    }

}
