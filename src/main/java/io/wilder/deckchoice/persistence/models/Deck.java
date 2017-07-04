package io.wilder.deckchoice.persistence.models;


import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
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
			deckInterface.insert(deck.getDeckId(), deck.getName());
		} finally {
			h.close();
		}
	}

	private interface DecksInterface {
		@SqlUpdate("insert into deck (deck_id, name) values (:deckId, :name)")
		int insert(@Bind("deckId") int deckId, @Bind("name") String name);
	}
}
