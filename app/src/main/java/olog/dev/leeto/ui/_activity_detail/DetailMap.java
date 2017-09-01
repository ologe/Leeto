package olog.dev.leeto.ui._activity_detail;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import olog.dev.leeto.data.model.Journey;
import olog.dev.leeto.data.model.Stop;
import olog.dev.leeto.ui.custom_view.RoundedMapView;

public class DetailMap extends RoundedMapView implements OnMapReadyCallback, LifecycleObserver {

    private static final int ZOOM = 13;

    private Journey journey;

    private CompositeDisposable subscriptions;

    public DetailMap(Context context) {
        this(context, null);
    }

    public DetailMap(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DetailMap(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void init(Journey journey){
        this.journey = journey;
        super.onCreate(null);
        getMapAsync(this);
        subscriptions = new CompositeDisposable();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void start(){
        super.onStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume(){
        super.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause(){
        super.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stop(){
        super.onStop();
        subscriptions.clear();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy(){
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Disposable markerDisposable = Single.just(journey.getStopList())
                .flatMapObservable(Observable::fromIterable)
                .map(Stop::getLocation)
                .map(location -> Pair.create(location, new LatLng(location.getLatitude(), location.getLongitude())))
                .map(pair -> new MarkerOptions().position(pair.second).title(pair.first.getAddress()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(googleMap::addMarker, Throwable::printStackTrace);

        Disposable moveCameraDisposable = Single.just(journey.getStopList())
                .map(stopList -> stopList.get(0))
                .map(Stop::getLocation)
                .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                .map(latLng -> CameraUpdateFactory.newLatLngZoom(latLng, ZOOM))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(googleMap::moveCamera, Throwable::printStackTrace);

        subscriptions.addAll(markerDisposable, moveCameraDisposable);
    }


//        builder.include(latLng);
//        markerList.add(marker);
//        markerList.get(0).showInfoWindow();

//    public void moveTo(Location location, int position){
//        getMapAsync(googleMap -> {
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//            markerList.get(position).showInfoWindow();
//
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
//        });
//    }
//
    public void addMarker(Stop stop){
//        getMapAsync(googleMap -> {
//
//            Location location = stop.getLocation();
//
//            Marker marker = googleMap.addMarker(
//                    new MarkerOptions()
//                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                            .title(location.getAddress()));
//            markerList.add(marker);
//        });
    }
}
