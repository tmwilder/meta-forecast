SELECT * from
  (SELECT player, sum(wins) as total_wins, sum(losses) as total_losses, sum(draws) as total_draws, (sum(wins) * 1.0 / (sum(draws) + sum(losses) + sum(wins))) as winrate FROM
    (SELECT
       p1.name as player,
       sum(CASE WHEN (m.result = 'PLAYER_1_WIN') then 1 else 0 END ) as wins,
       sum(CASE WHEN (m.result = 'PLAYER_2_WIN') then 1 else 0 END ) as losses,
       sum(CASE WHEN (m.result = 'UNINTENTIONAL_DRAW') then 1 else 0 END) as draws
     FROM matches m
       INNER JOIN players p1 on p1.player_id = m.player_1
       inner join tournaments t on m.tournament_id = t.tournament_id and t.date_played >= '2016-07-29'
     GROUP BY m.player_1

     UNION ALL

     SELECT
       p2.name as player,
       sum(CASE WHEN (m.result = 'PLAYER_2_WIN') then 1 else 0 END ) as wins,
       sum(CASE WHEN (m.result = 'PLAYER_1_WIN') then 1 else 0 END ) as losses,
       sum(CASE WHEN (m.result = 'UNINTENTIONAL_DRAW') then 1 else 0 END) as draws
     FROM matches m
       INNER JOIN players p2 on p2.player_id = m.player_2
       inner join tournaments t on m.tournament_id = t.tournament_id and t.date_played >= '2016-07-29'
     GROUP BY m.player_2)
  GROUP BY player
   ORDER BY winrate desc)
WHERE (total_wins + total_losses) > 30;

