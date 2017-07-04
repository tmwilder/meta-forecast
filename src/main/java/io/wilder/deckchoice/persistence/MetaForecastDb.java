package io.wilder.deckchoice.persistence;


import java.nio.file.Files;
import java.nio.file.Paths;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.sqlite.SQLiteDataSource;

public class MetaForecastDb {
	private static final String DB_FILEPATH = "/tmp/data.db";
	private static final String DB_LOCATION = "jdbc:sqlite:" + DB_FILEPATH;

	public static void wipeDatabase(){
		try {
			Files.deleteIfExists(Paths.get(DB_FILEPATH));
		} catch (Exception e){
			System.out.println("Could not delete DB file, may not exist: " + DB_LOCATION);
		}
	}

	public static void applySchema(){
		DBI dbi = getDbi();
		Handle h = dbi.open();
		h.execute(
				"CREATE TABLE players (" +
						"player_id integer primary key, " +
						"name varchar(64), " +
						"is_local boolean" +
						")"
		);

		h.execute(
				"CREATE TABLE decks (" +
						"deck_id integer primary key, " +
						"name varchar(64) " +
						")"
		);

		h.execute(
				"CREATE TABLE tournaments (" +
						"tournament_id integer primary key, " +
						"city varchar(64), " +
						"store varchar(64), " +
						"date_played date " +
						")"
		);

		h.execute(
				"CREATE TABLE matches (" +
						"match_id integer primary key, " +
						"player_1 integer, " +
						"player_2 integer, " +
						"player_1_deck integer, " +
						"player_2_deck integer, " +
						"player_1_wins integer, " +
						"player_2_wins integer, " +
						"draws integer, " +
						"tournament_id integer, " +
						"result varchar(24), " + // PLAYER_1 PLAYER_2 DRAW PLAYER_1_MATCH_LOSS PLAYER_2_MATCH_LOSS INTENTIONAL_DRAW UNINTENTIONAL_DRAW
						"FOREIGN KEY (player_1) REFERENCES players (player_id) , " +
						"FOREIGN KEY (player_2) REFERENCES players (player_id) , " +
						"FOREIGN KEY (player_1_deck) REFERENCES decks (deck_id) , " +
						"FOREIGN KEY (player_2_deck) REFERENCES decks (deck_id) , " +
						"FOREIGN KEY (tournament_id) REFERENCES tournaments (tournament_id)" +
						")"
		);
	}

	public static DBI getDbi(){
		SQLiteDataSource ds = new SQLiteDataSource();
		ds.setUrl(DB_LOCATION);
		return new DBI(ds);
	}
}