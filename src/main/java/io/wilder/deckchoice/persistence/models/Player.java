package io.wilder.deckchoice.persistence.models;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindMap;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
	private int playerId;
	private String playerName;
	private Boolean isLocal;
	private Integer elo;

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

	public static List<Player> getAll(DBI dbi){
		Handle h = dbi.open();
		try {
			PlayersInterface playersInterface = h.attach(PlayersInterface.class);
			return playersInterface.getAll();
		} finally {
			h.close();
		}
	}

	public static int setElo(DBI dbi, Integer newElo, Integer playerId){
		Handle h = dbi.open();
		try {
			PlayersInterface playersInterface = h.attach(PlayersInterface.class);
			return playersInterface.setElo(newElo, playerId);
		} finally {
			h.close();
		}
	}

	private interface PlayersInterface {
		@SqlUpdate("insert into players (name, is_local) values (:playerName, :isLocal)")
		int insert(@Bind("playerName") String playerName, @Bind("isLocal") boolean isLocal);

		@SqlQuery("select player_id from players where name = :playerName and is_local = :isLocal")
		Integer getIdByPlayerName(@Bind("playerName") String playerName, @Bind("isLocal") boolean isLocal);

		@SqlQuery("select * from players")
		@Mapper(PlayerMapper.class)
		List<Player> getAll();

		@SqlUpdate("update players set elo = :new_elo where player_id = :player_id")
		int setElo(@Bind("new_elo") Integer newElo, @Bind("player_id") Integer playerId);
	}

	public static class PlayerMapper implements ResultSetMapper<Player> {
		public Player map(int index, ResultSet r, StatementContext ctx) throws SQLException {
			return Player.builder()
					.playerId(r.getInt("player_id"))
					.isLocal(r.getBoolean("is_local"))
					.playerName(r.getString("name"))
					.elo(r.getInt("elo"))
					.build();
		}
	}
}
