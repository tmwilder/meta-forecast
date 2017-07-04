package io.wilder.deckchoice.persistence.models;


import io.wilder.deckchoice.enums.Result;
import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

@Builder
@Getter
public class Match {
	private int matchId;
	private int player1;
	private int player2;
	private int player1Deck;
	private int player2Deck;
	private int player1Wins;
	private int player2Wins;
	private int draws;
	private int tournamentId;
	private Result result;

	public static void persist(Match match, DBI dbi){
		Handle h = dbi.open();
		try {
			MatchesInterface matchesInterface = h.attach(MatchesInterface.class);
			matchesInterface.insert(
					match.getTournamentId(),
					match.getPlayer1(),
					match.getPlayer2(),
					match.getPlayer1Deck(),
					match.getPlayer2Deck(),
					match.getPlayer1Wins(),
					match.getPlayer2Wins(),
					match.getDraws(),
					match.getTournamentId(),
					match.getResult().name());
		} finally {
			h.close();
		}
	}

	private interface MatchesInterface {
		@SqlUpdate(
				"insert into tournaments " +
						"(match_id, player_1, player_2, player_1_deck, player_2_deck, player_1_wins, player_2_wins, draws, tournament_id, result) " +
						"values (:matchId, :player1, :player2, :player1Deck, :player2Deck, :player1Wins, :player2Wins, :draws, :tournamentId, :result)"
		)
		int insert(
				@Bind("matchId") int matchId,
				@Bind("player1") int player1,
				@Bind("player2") int player2,
				@Bind("player1Deck") int player1Deck,
				@Bind("player2Deck") int player2Deck,
				@Bind("player1Wins") int player1Wins,
				@Bind("player2Wins") int player2Wins,
				@Bind("draws") int draws,
				@Bind("tournamentId") int tournamentId,
				@Bind("result") String result
		);
	}
}
