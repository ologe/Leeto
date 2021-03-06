package olog.dev.leeto.ui.navigator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import olog.dev.leeto.dagger.PerActivity;
import olog.dev.leeto.ui._activity_detail.DetailActivity;
import olog.dev.leeto.ui._activity_main.MainActivity;
import olog.dev.leeto.ui.activity_add_journey.AddJourneyActivity;

@PerActivity
public class Navigator {

    private final AppCompatActivity activity;

    @Inject
    Navigator(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void toDetailActivity(@NonNull List<WeakReference<View>> views,
                                 long journeyId) {

        // try to add navigation bar to hero transition
        View decorView = activity.getWindow().getDecorView();
        if(decorView != null){
            View navigationBar = decorView.findViewById(android.R.id.navigationBarBackground);
            if(navigationBar != null){
                navigationBar.setTransitionName(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                views.add(new WeakReference<>(navigationBar));
            }
        }

        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.BUNDLE_JOURNEY_ID, journeyId);

        @SuppressWarnings("unchecked")
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, createPairs(views));

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void toAddJourneyActivity(@NonNull FloatingActionButton fab) {
        Intent intent = new Intent(activity, AddJourneyActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                activity, fab, fab.getTransitionName());
        ActivityCompat.startActivityForResult(activity, intent, MainActivity.ADD_JOURNEY_REQUEST_CODE, options.toBundle());
    }

    public void toAddStopActivity(@NonNull FloatingActionButton fab) {
//        Intent intent = new Intent(activity, AddStopActivity.class);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
//                activity, fab, fab.getTransitionName());
//        activity.startActivity(intent, options.toBundle());
    }

    private Pair[] createPairs(List<WeakReference<View>> views){
        Pair[] pairs = new Pair[views.size()];

        int i = 0;

        for (WeakReference<View> view : views) {
            View v = view.get();
            if (v != null){
                pairs[i] = Pair.create(v, v.getTransitionName());
            }

            i++;
        }

        return pairs;
    }

}
