package com.delect.test.java;

public class Starter {
    public static void main(String[] args) {
        AppComponent appComponent = DaggerAppComponent.create();
        try {
            Class<?> daggerReflectClass = Class.forName("dagger.reflect.DaggerReflect");
            System.out.println("dagger reflect class is available");
        } catch (ClassNotFoundException e) {
            System.out.println("dagger reflect class not available");
        }
    }
}
