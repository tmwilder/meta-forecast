package io.wilder.deckchoice.etl;

import io.wilder.deckchoice.etl.mox.MatchDataBean;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class MoxDataEtlTest {

	/**
	 * Ensure duped inputs are deduped.
	 */
	@Test
	public void testDedupeMatchInputs(){
		MatchDataBean beanA = new MatchDataBean();
		MatchDataBean beanB = new MatchDataBean();

		LocalDate playedOnDate = LocalDate.now();

		beanA.setPlayer("Player");
		beanA.setOpponent("Opponent");
		beanA.setCity("Seattle");
		beanA.setStore("CK");
		beanA.setRound("3");
		beanA.setPlayedOnDate(playedOnDate);

		beanB.setPlayer("Opponent");
		beanB.setOpponent("Player");
		beanB.setCity("Seattle");
		beanB.setStore("CK");
		beanB.setRound("3");
		beanB.setPlayedOnDate(playedOnDate);

		List<MatchDataBean> beanList = Arrays.asList(beanA, beanB);

		List<MatchDataBean> uniqueBeanList = MoxDataEtl.dedupeMatchInputs(beanList);
		Assert.assertEquals(1, uniqueBeanList.size());
	}

	/**
	 * Ensure duped inputs are deduped.
	 */
	@Test
	public void testDedupeMatchInputsNoDupe(){
		MatchDataBean beanA = new MatchDataBean();
		MatchDataBean beanB = new MatchDataBean();

		LocalDate playedOnDate = LocalDate.now();

		beanA.setPlayer("Player");
		beanA.setOpponent("Opponent");
		beanA.setCity("Portland");
		beanA.setStore("CK");
		beanA.setRound("4");
		beanA.setPlayedOnDate(playedOnDate);

		beanB.setPlayer("Opponent");
		beanB.setOpponent("Player");
		beanB.setCity("Seattle");
		beanB.setStore("CK");
		beanB.setRound("3");
		beanB.setPlayedOnDate(playedOnDate);

		List<MatchDataBean> beanList = Arrays.asList(beanA, beanB);

		List<MatchDataBean> uniqueBeanList = MoxDataEtl.dedupeMatchInputs(beanList);
		Assert.assertEquals(2, uniqueBeanList.size());
	}
}