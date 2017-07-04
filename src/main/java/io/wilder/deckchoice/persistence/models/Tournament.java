package io.wilder.deckchoice.persistence.models;


import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

@Builder
@Getter
public class Tournament {
	private int tournamentId;
	private String city;
	private String store;
	private LocalDate datePlayed;

	public static void persist(Tournament tournament, DBI dbi){
		Handle h = dbi.open();
		try {
			TournamentstInterface tournamentstInterface = h.attach(TournamentstInterface.class);
			tournamentstInterface.insert(
					tournament.getTournamentId(),
					tournament.getCity(),
					tournament.getStore(),
					tournament.getDatePlayed());
		} finally {
			h.close();
		}
	}

	private interface TournamentstInterface {
		@SqlUpdate(
				"insert into tournaments " +
					"(tournament_id, city, store, date_played) " +
					"values (:tournamentId, :city, :store, :datePlayed)")
		int insert(
				@Bind("tournamentId") int tournamentId,
				@Bind("city") String city,
				@Bind("store") String store,
				@Bind("datePlayed") LocalDate datePlayed);
	}
}
