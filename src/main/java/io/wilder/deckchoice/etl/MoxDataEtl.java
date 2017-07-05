package io.wilder.deckchoice.etl;


import com.opencsv.bean.CsvToBeanBuilder;
import io.wilder.deckchoice.enums.Result;
import io.wilder.deckchoice.etl.mox.MatchDataBean;
import io.wilder.deckchoice.etl.mox.MoxPlayerAliases;
import io.wilder.deckchoice.persistence.MetaForecastDb;
import io.wilder.deckchoice.persistence.models.Deck;
import io.wilder.deckchoice.persistence.models.Match;
import io.wilder.deckchoice.persistence.models.Player;
import io.wilder.deckchoice.persistence.models.Tournament;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.skife.jdbi.v2.DBI;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


public class MoxDataEtl {

	public static void etl(){
		List<MatchDataBean> matchData = extract();
		List<Match> matches = transform(matchData);
		load(matches);
	}

	private static List<MatchDataBean> extract(){
		Resource[] resources = getResources();

		List<MatchDataBean> allMatches = new LinkedList<>();
		for (int i = 0; i < resources.length; i++){
			try {
				Resource resource = resources[i];

				List<MatchDataBean> beans = new CsvToBeanBuilder<>(new FileReader(resource.getFile()))
						.withType(MatchDataBean.class)
						.withSeparator(',')
						.withSkipLines(1)
						.build()
						.parse();

				String city = getCity(resource);
				String store = getStore(resource);
				LocalDate playedOnDate = getPlayedOnDate(resource);

				for (MatchDataBean bean: beans) {
					bean.setCity(city);
					bean.setStore(store);
					bean.setPlayedOnDate(playedOnDate);
				}

				allMatches.addAll(beans);

			} catch (FileNotFoundException e){
				throw new RuntimeException(e);
			} catch (IOException e){
				throw new RuntimeException(e);
			}
		}
		return allMatches;
	}

	private static String getCity(Resource resource){
		return "seattle";
	}

	private static String getStore(Resource resource){
		if (resource.getFilename().toLowerCase().contains("mbh")){
			return "mbh";
		} else {
			return "ck";
		}
	}

	private static List<MatchDataBean> dedupeMatchInputs(List<MatchDataBean> matchDataBeans){
		Set<MatchDataBean> matchDataBeanSet = new HashSet<>();
		for (MatchDataBean matchDataBean: matchDataBeans){
			matchDataBeanSet.add(matchDataBean);
		}
		List<MatchDataBean> dedupedMatchData = new ArrayList<>();
		for (MatchDataBean dedupedMatchDataBean: matchDataBeanSet) {
			dedupedMatchData.add(dedupedMatchDataBean);
		}
		return dedupedMatchData;
	}

	private static LocalDate getPlayedOnDate(Resource resource){
		Pattern pattern = Pattern.compile("[0-9]{8}");
		Matcher matcher = pattern.matcher(resource.getFilename());
		if (matcher.find()) {
			return LocalDate.parse(matcher.group(0), DateTimeFormatter.BASIC_ISO_DATE);
		} else {
			throw new RuntimeException("Could not parse date from input file: " + resource.getFilename());
		}
	}

	private static List<Match> transform(List<MatchDataBean> matchData){
		matchData = dedupeMatchInputs(matchData);

		List<Match> matches = new LinkedList<>();
		for (MatchDataBean matchDataBean: matchData){

			String player = matchDataBean.getPlayer();
			String opponent = matchDataBean.getOpponent();

			if (MoxPlayerAliases.playerAliases.containsKey(player)){
				player = MoxPlayerAliases.playerAliases.get(player);
			}

			if (MoxPlayerAliases.playerAliases.containsKey(opponent)){
				opponent = MoxPlayerAliases.playerAliases.get(opponent);
			}

			Match match = Match.builder()
				.draws(matchDataBean.getDrew())
				.player1Name(player)
				.player2Name(opponent)
				.player1Wins(matchDataBean.getWon())
				.player2Wins(matchDataBean.getLost())
				.player1DeckName(matchDataBean.getArchetype())
				.player2DeckName(matchDataBean.getOpponentArchetype())
				.draws(matchDataBean.getDrew())
				.city(matchDataBean.getCity())
				.store(matchDataBean.getStore())
				.playedOnDate(matchDataBean.getPlayedOnDate())
				.result(
						Result.ofCsvResult(matchDataBean.getResult()))
				.build();
			matches.add(match);
		}
		return matches;
	}

	private static void load(List<Match> matches){
		// Input has multiple results per player, tournament, dewck only persist once - then cache the ID.
		Map<String, Integer> tournamentsToIds = new HashMap<>();
		Map<String, Integer> playersToIds = new HashMap<>();
		Map<String, Integer> decksToIds = new HashMap<>();

		DBI foreCastDbi = MetaForecastDb.getDbi();

		for (Match match: matches) {
			if (match.getResult().equals(Result.BYE)){
				System.out.println("Skipping BYE result as we don't want these for analysis right now.");
				continue;
			}

			String tournamentKey = match.getCity() + match.getStore() + match.getPlayedOnDate().toString();

			if (!tournamentsToIds.containsKey(tournamentKey)){
				Tournament tournament = Tournament.builder()
					.city(match.getCity())
					.store(match.getStore())
					.datePlayed(match.getPlayedOnDate())
					.build();

				Tournament.persist(tournament, foreCastDbi);
				Integer tournamentId = Tournament.getIdByKeyValues(
					match.getCity(),
					match.getStore(),
					match.getPlayedOnDate(),
					foreCastDbi);
				tournamentsToIds.put(tournamentKey, tournamentId);
			}

			// We make the assumption that Seattle player names are unique, which they are to date -
			// there's no data in the CSV like DCI# to disambiguate otherwise, so there is no real recourse anyways.
			if (!playersToIds.containsKey(match.getPlayer1Name())){
				Player player = Player.builder()
					.playerName(match.getPlayer1Name())
					.isLocal(true)
					.build();
				Player.persist(player, foreCastDbi);

				Integer playerId = Player.getIdByPlayerName(match.getPlayer1Name(), true, foreCastDbi);
				playersToIds.put(player.getPlayerName(), playerId);
			}

			if (!playersToIds.containsKey(match.getPlayer2Name())){
				Player player = Player.builder()
						.playerName(match.getPlayer2Name())
						.isLocal(true)
						.build();
				Player.persist(player, foreCastDbi);

				Integer playerId = Player.getIdByPlayerName(match.getPlayer2Name(), true, foreCastDbi);
				playersToIds.put(player.getPlayerName(), playerId);
			}


			// Ensure deck also loaded.
			if (!decksToIds.containsKey(match.getPlayer1DeckName())){
				Deck deck = Deck.builder()
						.name(match.getPlayer1DeckName())
						.build();
				Deck.persist(deck, foreCastDbi);

				Integer deckId = Deck.getIdByDeckName(match.getPlayer1DeckName(), foreCastDbi);
				decksToIds.put(match.getPlayer1DeckName(), deckId);
			}

			if (!decksToIds.containsKey(match.getPlayer2DeckName())){
				Deck deck = Deck.builder()
						.name(match.getPlayer2DeckName())
						.build();
				Deck.persist(deck, foreCastDbi);

				Integer deckId = Deck.getIdByDeckName(match.getPlayer2DeckName(), foreCastDbi);
				decksToIds.put(match.getPlayer2DeckName(), deckId);
			}

			try {
				match.setPlayer1(playersToIds.get(match.getPlayer1Name()));
				match.setPlayer2(playersToIds.get(match.getPlayer2Name()));
				match.setPlayer1Deck(decksToIds.get(match.getPlayer1DeckName()));
				match.setPlayer2Deck(decksToIds.get(match.getPlayer2DeckName()));
				match.setTournamentId(tournamentsToIds.get(tournamentKey));
 			} catch (NullPointerException e){
				System.out.println("Invalid data for write entry, skipping, investigate.");
				continue;
			}

			Match.persist(match, foreCastDbi);
		}
	}

	private static Resource[] getResources(){
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			return resolver.getResources("classpath:io/wilder/deckchoice/data/mox/*.csv");
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
}
