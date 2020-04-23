package me.ahaliulang.testdemo.singleton;

/**
 * author:tdn
 * time:2020/1/15
 * description:
 */
public class Singleton {

    private final String str = "";

    public static final Singleton getInstance() {
        return SingletonHoler.INSTANCE;
    }

    private static class SingletonHoler {
        private static final Singleton INSTANCE = new Singleton();
    }
}
