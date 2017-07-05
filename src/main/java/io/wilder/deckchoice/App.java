package io.wilder.deckchoice;

import io.wilder.deckchoice.persistence.MetaForecastDb;


public class App {
    public static void createNewDatabase() {
        MetaForecastDb.wipeDatabase();
        MetaForecastDb.applySchema();
    }

    public static void main(){

    }
}
