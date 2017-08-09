package olog.dev.leeto.model.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import olog.dev.leeto.dagger.annotation.ApplicationContext;
import olog.dev.leeto.dagger.annotation.PerApplication;
import olog.dev.leeto.model.pojo.Journey;
import olog.dev.leeto.model.pojo.Stop;
import olog.dev.leeto.utility.rx.BaseSchedulerProvider;

@PerApplication
public class Repository implements IRepository {

    private static final String JOURNEY_LIST_FILE_NAME = "journeysList.json";

    private Context context;
    private List<Journey> journeyList;
    private BehaviorSubject<List<Journey>> publisher;
    private BaseSchedulerProvider schedulerProvider;

    @Inject
    Repository(@ApplicationContext Context context,
               BaseSchedulerProvider schedulerProvider){
        this.context = context;
        this.schedulerProvider = schedulerProvider;
        publisher = BehaviorSubject.create();
        journeyList = new ArrayList<>();

        // no null values
        publisher.onNext(journeyList);
        // can cause small leak
        loadFromStorage()
                .subscribeOn(this.schedulerProvider.io())
                .observeOn(this.schedulerProvider.mainThread())
                .subscribe(journeys -> {
                    journeyList = journeys;
                    publisher.onNext(journeyList);
                }, Throwable::printStackTrace);
    }

    @NonNull
    @Override
    public Observable<List<Journey>> observeToData(){
        return publisher;
    }

    @NonNull
    private Single<List<Journey>> loadFromStorage(){
        return Single.create( emitter -> {

            InputStream inputStream = context.openFileInput(JOURNEY_LIST_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String received;
            StringBuilder stringBuilder = new StringBuilder();

            while ((received = bufferedReader.readLine()) != null){
                stringBuilder.append(received);
            }
            inputStream.close();

            String jsonOutput = stringBuilder.toString();

            Gson loadedData = new Gson();
            journeyList = loadedData.fromJson(jsonOutput, new TypeToken<List<Journey>>(){}.getType());

            emitter.onSuccess(journeyList);
        });
    }

    @Override
    public void addJourney(@NonNull Journey journey){
        journeyList.add(journey);
        publisher.onNext(journeyList);

        saveJourneysList()
                .subscribeOn(schedulerProvider.io())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    @Override
    public void deleteJourney(@NonNull Journey journey) {
        journeyList.remove(journey);
        publisher.onNext(journeyList);

        saveJourneysList()
                .subscribeOn(schedulerProvider.io())
                .subscribe(() -> {}, Throwable::printStackTrace);
    }

    private Completable saveJourneysList(){
        return Completable.create(emitter -> {

            final String json = new Gson().toJson(journeyList, new TypeToken<List<Journey>>(){}.getType());

            OutputStreamWriter outputStream = new OutputStreamWriter(
                    context.openFileOutput(JOURNEY_LIST_FILE_NAME, Context.MODE_PRIVATE));
            outputStream.write(json);
            outputStream.close();

            emitter.onComplete();
        });
    }

    public void addStopToJourney(Context context, Journey j, Stop stop){
//        Single.create(e -> {
//            int index = -1;
//            for (int i = 0; i < journeyList.size(); i++) {
//                if(j.getName().equals(journeyList.get(i).getName())){
//                    index = i;
//                    break;
//                }
//            }
//
//            Journey journey = journeyList.get(index);
//            journey.addStop(stop);
//
//            saveJourneysList(context)
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(v -> {}, Throwable::printStackTrace);
//
//            e.onSuccess(true);
//
//        }).observeOn(Schedulers.io())
//                .subscribe(v -> {}, Throwable::printStackTrace);

    }

}
