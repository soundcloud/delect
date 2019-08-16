package com.delect.test.java;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Component
public interface AppComponent {

    @Module
    abstract class AppModule {
        @Provides Foo provideFoo() {
            return new Foo();
        }
    }
}
