SELECT test_deck, opposing, sum(wins) as total_wins, sum(losses) as total_losses, sum(draws) as total_draws FROM
(SELECT
  d1.name as test_deck,
  d2.name as opposing,
  sum(CASE WHEN (m.result = 'PLAYER_1_WIN') then 1 else 0 END ) as wins,
  sum(CASE WHEN (m.result = 'PLAYER_2_WIN') then 1 else 0 END ) as losses,
  sum(CASE WHEN (m.result = 'UNINTENTIONAL_DRAW') then 1 else 0 END) as draws
FROM matches m
INNER JOIN  decks d1 on d1.deck_id = m.player_1_deck
INNER JOIN  decks d2 on d2.deck_id = m.player_2_deck
GROUP BY m.player_1_deck, m.player_2_deck

UNION ALL

SELECT
  d1.name as test_deck,
  d2.name as opposing,
  sum(CASE WHEN (m.result = 'PLAYER_2_WIN') then 1 else 0 END ) as wins,
  sum(CASE WHEN (m.result = 'PLAYER_1_WIN') then 1 else 0 END ) as losses,
  sum(CASE WHEN (m.result = 'UNINTENTIONAL_DRAW') then 1 else 0 END) as draws
FROM matches m
  INNER JOIN  decks d1 on d1.deck_id = m.player_2_deck
  INNER JOIN  decks d2 on d2.deck_id = m.player_1_deck
GROUP BY m.player_1_deck, m.player_2_deck)

GROUP BY test_deck, opposing;

