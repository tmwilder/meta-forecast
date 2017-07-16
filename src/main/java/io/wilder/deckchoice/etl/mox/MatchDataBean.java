package io.wilder.deckchoice.etl.mox;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.opencsv.bean.CsvBindByPosition;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchDataBean {
	@CsvBindByPosition(position = 0)
	private String round;

	@CsvBindByPosition(position = 1)
	private String player;

	@CsvBindByPosition(position = 2)
	private String archetype;

	@CsvBindByPosition(position = 3)
	private String subArchetype;

	@CsvBindByPosition(position = 4)
	private String result;

	@CsvBindByPosition(position = 5)
	private int won;

	@CsvBindByPosition(position = 6)
	private int lost;

	@CsvBindByPosition(position = 7)
	private int drew;

	@CsvBindByPosition(position = 8)
	private String opponent;

	@CsvBindByPosition(position = 9)
	private String opponentArchetype;

	@CsvBindByPosition(position = 10)
	private String opponentSubArchetype;

	private LocalDate playedOnDate;

	private String city;

	private String store;

	/**
	 * Used to dedupe doubled rows for playera, playerb and playerb, playera each round.
	 */
	@Override
	public int hashCode(){
		List<String> hashData = new ArrayList<>(6);
		hashData.add(playedOnDate.toString());
		hashData.add(city);
		hashData.add(store);
		if (player != null){
			hashData.add(player);
		}
		if (opponent != null){
			hashData.add(opponent);
		}
		hashData.add(round);
		try {
			Collections.sort(hashData);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("At least one missing field found for parsed CSV data.");
		}
		HashFunction hashFunction = Hashing.md5();
		Hasher hasher = hashFunction.newHasher();
		for (String hashValue: hashData) {
			hasher.putString(hashValue, Charsets.UTF_8);
		}
		return hasher.hash().asInt();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return (obj.hashCode() == this.hashCode());
	}
}
