package io.wilder.deckchoice.elo;


import io.wilder.deckchoice.enums.Result;
import io.wilder.deckchoice.persistence.MetaForecastDb;
import io.wilder.deckchoice.persistence.models.Match;
import io.wilder.deckchoice.persistence.models.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;


public class Elo {
	private static final int STARTING_ELO = 1600;
	private static final int PROVISIONAL_K_VALUE = 32;
	private static final int K_VALUE = 32;

	public static void updateElos(){
		DBI foreCastDbi = MetaForecastDb.getDbi();
		List<Match> matches = Match.getAll(foreCastDbi);
		List<Player> players = Player.getAll(foreCastDbi);

		Map<Integer, Integer> playerElos = runElo(matches, players);
		persistElo(playerElos, foreCastDbi);
	}

	static  Map<Integer, Integer> runElo(List<Match> matches, List<Player> players){
		// K player_id, V elo
		Map<Integer, Integer> playerElos = new HashMap<>();

		for (Player player: players){
			playerElos.put(player.getPlayerId(), STARTING_ELO);
		}

		for (Match match: matches){
			Integer player1Elo = playerElos.get(match.getPlayer1());
			Integer player2Elo = playerElos.get(match.getPlayer2());
			Result result = match.getResult();
			Optional<NewPlayerElos> newPlayerElos = getNewPlayerElos(player1Elo, player2Elo, result);
			if (newPlayerElos.isPresent()){
				playerElos.put(match.getPlayer1(), newPlayerElos.get().getNewRatingPlayer1());
				playerElos.put(match.getPlayer2(), newPlayerElos.get().getNewRatingPlayer2());
			}
		}
		return playerElos;
	}


	/**
	 * Gets new player elos after a match concludes. If the result was not a win, loss or draw (e.g. rules losses, ID)
	 * then we do not update elo.
	 */
	static Optional<NewPlayerElos> getNewPlayerElos(Integer player1RealRating, Integer player2RealRating, Result result){

		double player1ExpectedOutcome = getExpectedPlayerOutcome(player1RealRating, player2RealRating); // Expected % win
		double player2ExpectedOutcome = getExpectedPlayerOutcome(player2RealRating, player1RealRating); // Expected % win
		Integer k = K_VALUE;

		double player1Outcome; // win - 1, Draw 0.5, Loss-0
		double player2Outcome;

		double newRatingPLayer1;
		double newRatingPlayer2;

		switch (result){
			case PLAYER_1_WIN:
				player1Outcome = 1;
				player2Outcome = 0;
				break;
			case PLAYER_2_WIN:
				player1Outcome = 0;
				player2Outcome = 1;
				break;
			case UNINTENTIONAL_DRAW:
				player1Outcome = .5;
				player2Outcome = .5;
				break;
			default:
				return Optional.empty();
		}

		newRatingPLayer1 = getNewRating(player1RealRating, player1Outcome, player1ExpectedOutcome);
		newRatingPlayer2 = getNewRating(player2RealRating, player2Outcome, player2ExpectedOutcome);

		return Optional.of(
				NewPlayerElos.builder()
						.newRatingPlayer1((int) newRatingPLayer1)
						.newRatingPlayer2((int) newRatingPlayer2)
						.build());
	}

	static double getExpectedPlayerOutcome(Integer playerARating, Integer playerBRating){
		return 1 / (1 + Math.pow(10, ((playerBRating - playerARating) / 400.0)));
	}

	static double getNewRating(Integer playerRating, double outcome, double expectedOutcome){
		// Use provisional K Value for new players.
		return Math.ceil(playerRating + K_VALUE * (outcome - expectedOutcome));
	}

	@Builder
	@Getter
	static class NewPlayerElos {
		Integer newRatingPlayer1;
		Integer newRatingPlayer2;
	}

	static void persistElo(Map<Integer, Integer> playerElos, DBI forecastDbi){
		for (Map.Entry<Integer, Integer> playerElo: playerElos.entrySet()) {
			System.out.println(
					"Updating player id: " + playerElo.getKey().toString() +
						" to elo: " + playerElo.getValue().toString());
			Player.setElo(forecastDbi, playerElo.getValue(), playerElo.getKey());
		}
	}
}
