package io.wilder.deckchoice;


import io.wilder.deckchoice.etl.MoxDataEtl;
import io.wilder.deckchoice.persistence.MetaForecastDb;

public class App {

    public static void main(){
        MetaForecastDb.wipeDatabase();
        MetaForecastDb.applySchema();
        MoxDataEtl.etl();
    }
}
