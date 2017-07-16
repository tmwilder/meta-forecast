package io.wilder.deckchoice.persistence.models;


import io.wilder.deckchoice.enums.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
	private int matchId;

	private String player1Name;
	private String player2Name;
	private int player1;
	private int player2;

	private String player1DeckName;
	private String player2DeckName;
	private int player1Deck;
	private int player2Deck;

	private int player1Wins;
	private int player2Wins;

	private int draws;
	private int tournamentId;
	private Result result;

	private String city;
	private String store;
	private LocalDate playedOnDate;

	public static void persist(Match match, DBI dbi){
		Handle h = dbi.open();
		try {
			MatchesInterface matchesInterface = h.attach(MatchesInterface.class);
			matchesInterface.insert(
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

	public static List<Match> getAll(DBI dbi){
		Handle h = dbi.open();
		try {
			MatchesInterface matchesInterface = h.attach(MatchesInterface.class);
			return matchesInterface.getAll();
		} finally {
			h.close();
		}
	}

	private interface MatchesInterface {
		@SqlUpdate(
				"insert into matches " +
						"(player_1, player_2, player_1_deck, player_2_deck, player_1_wins, player_2_wins, draws, tournament_id, result) " +
						"values (:player1, :player2, :player1Deck, :player2Deck, :player1Wins, :player2Wins, :draws, :tournamentId, :result)"
		)
		int insert(
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

		@SqlQuery(
				"select * from matches " +
						"inner join tournaments " +
							"on matches.tournament_id = tournaments.tournament_id " +
				"order by tournaments.date_played, matches.match_id asc")
		@Mapper(MatchMapper.class)
		List<Match> getAll();
	}

	public static class MatchMapper implements ResultSetMapper<Match> {
		public Match map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return Match.builder()
					.matchId(r.getInt("match_id"))
					.player1(r.getInt("player_1"))
					.player2(r.getInt("player_2"))
					.player1Deck(r.getInt("player_1_deck"))
					.player2Deck(r.getInt("player_2_deck"))
					.player1Wins(r.getInt("player_1_wins"))
					.player2Wins(r.getInt("player_2_wins"))
					.draws(r.getInt("draws"))
					.tournamentId(r.getInt("tournament_id"))
					.result(Result.valueOf(r.getString("result")))
					.build();
		}
	}
}
