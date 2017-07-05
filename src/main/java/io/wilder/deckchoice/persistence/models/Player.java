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
public class Player {
	private int playerId;
	private String playerName;
	private Boolean isLocal;

	public static void persist(Player player, DBI dbi){
		Handle h = dbi.open();
		try {
			PlayersInterface playersInterface = h.attach(PlayersInterface.class);
			playersInterface.insert(
					player.getPlayerName(),
					player.getIsLocal());
		} finally {
			h.close();
		}
	}

	public static Integer getIdByPlayerName(String playerName, boolean isLocal, DBI dbi){
		Handle h = dbi.open();
		try {
			PlayersInterface playersInterface = h.attach(PlayersInterface.class);
			return playersInterface.getIdByPlayerName(playerName, isLocal);
		} finally {
			h.close();
		}
	}

	private interface PlayersInterface {
		@SqlUpdate("insert into players (name, is_local) values (:playerName, :isLocal)")
		int insert(@Bind("playerName") String playerName, @Bind("isLocal") boolean isLocal);

		@SqlQuery("select player_id from players where name = :playerName and is_local = :isLocal")
		Integer getIdByPlayerName(@Bind("playerName") String playerName, @Bind("isLocal") boolean isLocal);
	}
}
