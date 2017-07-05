package io.wilder.deckchoice.persistence.models;


import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
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
					tournament.getCity(),
					tournament.getStore(),
					tournament.getDatePlayed());
		} finally {
			h.close();
		}
	}

	public static Integer getIdByKeyValues(String city, String store, LocalDate datePlayed, DBI dbi){
		Handle h = dbi.open();
		try {
			TournamentstInterface tournamentstInterface = h.attach(TournamentstInterface.class);
			return tournamentstInterface.getIdByKeyValues(city, store, datePlayed);
		} finally {
			h.close();
		}
	}

	private interface TournamentstInterface {
		@SqlUpdate(
				"insert into tournaments " +
					"(city, store, date_played) " +
					"values (:city, :store, :datePlayed)")
		int insert(
				@Bind("city") String city,
				@Bind("store") String store,
				@Bind("datePlayed") LocalDate datePlayed);

		@SqlQuery(
				"select tournament_id from tournaments " +
					"where city = :city and store = :store and date_played = :datePlayed")
		Integer getIdByKeyValues(
				@Bind("city") String city,
				@Bind("store") String store,
				@Bind("datePlayed") LocalDate datePlayed);
	}
}
