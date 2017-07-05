package io.wilder.deckchoice.etl;

import io.wilder.deckchoice.persistence.MetaForecastDb;
import org.junit.Test;

public class MoxDataEtlTest {

	@Test
	public void testEtl(){
		MetaForecastDb.wipeDatabase();
		MetaForecastDb.applySchema();
		MoxDataEtl.etl();
	}
}