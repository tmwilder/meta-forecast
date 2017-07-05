package io.wilder.deckchoice.persistence.models;


import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

@Builder
@Getter
public class Deck {
	private int deckId;
	private String name;

	public static void persist(Deck deck, DBI dbi){
		Handle h = dbi.open();
		try {
			DecksInterface deckInterface = h.attach(DecksInterface.class);
			deckInterface.insert(deck.getName());
		} finally {
			h.close();
		}
	}

	public static Integer getIdByDeckName(String deckName, DBI dbi){
		Handle h = dbi.open();
		try {
			DecksInterface decksInterface = h.attach(DecksInterface.class);
			return decksInterface.getIdByDeckName(deckName);
		} finally {
			h.close();
		}
	}

	private interface DecksInterface {
		@SqlUpdate("insert into decks (name) values (:name)")
		int insert(@Bind("name") String name);

		@SqlQuery("select deck_id from decks where name = :deckName")
		Integer getIdByDeckName(@Bind("deckName") String deckName);
	}
}
