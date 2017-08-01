package olog.dev.leeto.custom_view;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import olog.dev.leeto.R;
import olog.dev.leeto.activity_main.JourneyAdapter;
import olog.dev.leeto.utility.recycler_view.DragCallback;

public class ParallaxRecyclerView extends RecyclerView implements LifecycleObserver {

    private boolean isFabAdd = true;
    private static final int PIVOT = 200; // 200 == 1f(max alpha) - 1/200 (dy/200)

    private Lifecycle lifecycle;
    private View scrim;
    private View toolbar;
    private FloatingActionButton fab;

    private JourneyAdapter adapter;
    private LinearLayoutManager layoutManager;

    private CoordinatorLayout.LayoutParams scrimParams = null;

    int topMargin;

    public ParallaxRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // starting scrim accent top margin
        if(!isInEditMode()){
            topMargin = (int) (125 * getResources().getDisplayMetrics().density);

            layoutManager = new LinearLayoutManager(getContext());
            adapter = new JourneyAdapter();
            setLayoutManager(layoutManager);
            setAdapter(adapter);

            // swipe
            ItemTouchHelper helper = new ItemTouchHelper(
                    new DragCallback(getAdapter(),0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
            helper.attachToRecyclerView(this);
        }
    }

    public void attachLifecycle(@NonNull Lifecycle lifecycle){
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    public void setViews(View scrim, View toolbar, FloatingActionButton fab){
        this.scrim = scrim;
        this.toolbar = toolbar;
        this.fab = fab;

        scrimParams = (CoordinatorLayout.LayoutParams) scrim.getLayoutParams();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){
        addOnScrollListener(onScrollListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){
        removeOnScrollListener(onScrollListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        lifecycle.removeObserver(this);
        scrim = null;
        fab = null;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        private int totalDy = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            // after deleting an item, animation become buggy, so restore totalDy when
            // the list reach start
            if(!recyclerView.canScrollVertically(-1)){
                totalDy = 0;
            } else totalDy += dy;

            handleFabDrawable(recyclerView, totalDy);

            handleScrimParallax(totalDy, dy);
        }
    };

    private void handleFabDrawable(RecyclerView recyclerView, int totalDy){
        if(!recyclerView.canScrollVertically(-1)){
            // can't scroll up
            fab.setImageResource(R.drawable.vd_add);
            isFabAdd = true;
        }

        if(totalDy != 0){
            if(isFabAdd){
                isFabAdd = false;
                fab.setImageResource(R.drawable.vd_key_arrow_up);
            }
        }
    }

    private void handleScrimParallax(int totalDy, int dy){
        if(scrimParams == null) return;

        if(dy > 0){
            // moving up
            scrimParams.topMargin = Math.max(0, scrimParams.topMargin - dy/3);
            scrim.setLayoutParams(scrimParams);
            toolbar.setAlpha(Math.max(0f, toolbar.getAlpha() - (float)dy/200));
        } else {
            // moving down

            int firstVisible = getLayoutManager().findFirstCompletelyVisibleItemPosition();

            if(firstVisible <= 3){
                scrimParams.topMargin = Math.min(topMargin, scrimParams.topMargin - dy/3);
                scrim.setLayoutParams(scrimParams);
            }

            if(firstVisible <= 1 && totalDy <= PIVOT){
                toolbar.setAlpha(Math.min(.85f, toolbar.getAlpha() - (float)dy/200));
            }
        }
    }

    @Override
    public JourneyAdapter getAdapter() {
        return adapter;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public boolean isFabAdd() {
        return isFabAdd;
    }
}
