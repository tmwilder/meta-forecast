package io.wilder.deckchoice.persistence.models;


import lombok.Builder;
import lombok.Getter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
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
					player.getPlayerId(),
					player.getPlayerName(),
					player.getIsLocal());
		} finally {
			h.close();
		}
	}

	private interface PlayersInterface {
		@SqlUpdate(
				"insert into tournaments " +
						"(player_id, player_name, is_local) " +
						"values (:playerId, :playerName, :isLocal)")
		int insert(
				@Bind("playerId") int playerId,
				@Bind("playerName") String playerName,
				@Bind("isLocal") boolean isLocal);
	}
}
