package io.wilder.deckchoice.etl.mox;


import java.util.HashMap;
import java.util.Map;

public class MoxPlayerAliases {
	public static Map<String, String> playerAliases;
	static {
		playerAliases = new HashMap();
		playerAliases.put("Timothy Wilder", "Tim Wilder");
		playerAliases.put("Benjamin Ricahrds", "Benjamin Richards");
	}
}
