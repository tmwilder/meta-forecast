package io.wilder.deckchoice;


import io.wilder.deckchoice.elo.Elo;
import io.wilder.deckchoice.etl.MoxDataEtl;
import io.wilder.deckchoice.persistence.MetaForecastDb;

public class MetaForecastPipeline {
    public static void main(String[] args){
        MetaForecastDb.wipeDatabase();
        MetaForecastDb.applySchema();
        MoxDataEtl.etl();
        Elo.updateElos();
    }
}
