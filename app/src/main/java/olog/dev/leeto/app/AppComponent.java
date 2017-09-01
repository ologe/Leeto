package olog.dev.leeto.app;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import olog.dev.leeto.data.repository.RepositoryModule;
import olog.dev.leeto.ui._activity_detail.di.DetailActivityInjector;
import olog.dev.leeto.ui._activity_main.di.MainActivityInjector;
import olog.dev.leeto.ui.activity_add_journey.di.AddJourneyInjector;
import olog.dev.leeto.utility.dagger.annotations.scope.PerApplication;
import olog.dev.leeto.utility.reactive.ReactiveModule;

@Component(modules = {
        AppModule.class,
        RepositoryModule.class,
        ReactiveModule.class,
        AppHelperModule.class,

        AndroidSupportInjectionModule.class,
        MainActivityInjector.class,
        AddJourneyInjector.class,
        DetailActivityInjector.class
})
@PerApplication
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App>{

        abstract Builder appModule(AppModule module);

        @Override
        public void seedInstance(App instance) {
            appModule(new AppModule(instance));
        }

    }

}
