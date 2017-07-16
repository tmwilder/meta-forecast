package io.wilder.deckchoice.elo;

import io.wilder.deckchoice.enums.Result;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class EloTest {

	@Test
	public void testGetNewRating(){
		double result = Elo.getNewRating(1583, 1, .52);
		Assert.assertEquals(1591, result, .01);

		double result2 = Elo.getNewRating(1572, 0, .48);
		Assert.assertEquals(1565, result2, .01);

	}

	@Test
	public void testGetExpectedPlayerOutcome(){
		double result = Elo.getExpectedPlayerOutcome(1600, 1600);
		Assert.assertEquals(.5, result, .01);

		double result2 = Elo.getExpectedPlayerOutcome(1600, 1900);
		Assert.assertEquals(.15, result2, .01);

		double result3 = Elo.getExpectedPlayerOutcome(1900, 1600);
		Assert.assertEquals(.85, result3, .01);
	}

	@Test
	public void testGetNewPlayerElos() {
		Optional<Elo.NewPlayerElos> result = Elo.getNewPlayerElos(1511, 1810, Result.PLAYER_2_WIN);

		Assert.assertEquals(Integer.valueOf(1509), result.get().getNewRatingPlayer1());
		Assert.assertEquals(Integer.valueOf(1813), result.get().getNewRatingPlayer2());

		Optional<Elo.NewPlayerElos> result2 = Elo.getNewPlayerElos(1511, 1810, Result.PLAYER_2_RULES_WIN);
		Assert.assertEquals(Optional.empty(), result2);

		Optional<Elo.NewPlayerElos> result3 = Elo.getNewPlayerElos(1511, 1810, Result.PLAYER_1_WIN);
		Assert.assertEquals(Integer.valueOf(1525), result3.get().getNewRatingPlayer1());
		Assert.assertEquals(Integer.valueOf(1797), result3.get().getNewRatingPlayer2());

		Optional<Elo.NewPlayerElos> result4 = Elo.getNewPlayerElos(1511, 1810, Result.UNINTENTIONAL_DRAW);
		Assert.assertEquals(Integer.valueOf(1517), result4.get().getNewRatingPlayer1());
		Assert.assertEquals(Integer.valueOf(1805), result4.get().getNewRatingPlayer2());
	}
}
