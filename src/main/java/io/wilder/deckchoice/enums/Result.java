package io.wilder.deckchoice.enums;


import java.util.HashMap;
import java.util.Map;

public enum Result {
	PLAYER_1_WIN,
	PLAYER_2_WIN,
	UNINTENTIONAL_DRAW,
	INTENTIONAL_DRAW,
	PLAYER_1_RULES_WIN,
	PLAYER_2_RULES_WIN,
	BYE;

	private static Map<String, Result> csvResultsToResult;
	static {
		csvResultsToResult = new HashMap<>();
		csvResultsToResult.put("Won", Result.PLAYER_1_WIN);
		csvResultsToResult.put("Lost", Result.PLAYER_2_WIN);
		csvResultsToResult.put("Drew", Result.UNINTENTIONAL_DRAW);
		csvResultsToResult.put("ID", Result.INTENTIONAL_DRAW);
		csvResultsToResult.put("Bye", Result.BYE);
		csvResultsToResult.put("Match Loss", Result.PLAYER_2_RULES_WIN);
	}

	public static Result ofCsvResult(String csvResult) {
		Result result = csvResultsToResult.get(csvResult);
		if (result == null){
			throw new IllegalArgumentException("Could not map result to known type: " + csvResult);
		}
		return result;
	}
}
