select player_name, deck_name, date_played from (SELECT
                                                   p.name AS player_name,
                                                   d.name AS deck_name,
                                                   t.date_played AS date_played
                                                 FROM players p
                                                   INNER JOIN matches m
                                                     ON p.player_id = m.player_1
                                                   INNER JOIN decks d
                                                     ON d.deck_id = m.player_1_deck
                                                   INNER JOIN tournaments t
                                                     ON m.tournament_id = t.tournament_id
                                                 WHERE p.name IN (
                                                   "Adam Prusa",
                                                   "Alex Staver",
                                                   "Andy Stowell",
                                                   "Benjamin Richards",
                                                   "Brad Rutherford",
                                                   "Brian Bennett",
                                                   "Daniel Goh",
                                                   "Elston Cloy",
                                                   "Jeremy Edwards",
                                                   "Jordan Aisaka",
                                                   "Garth Brewe",
                                                   "Greg Mitchell",
                                                   "James Johnson",
                                                   "Martin Goldman-kirst",
                                                   "Matthew Staver",
                                                   "Michael Wallio",
                                                   "Miles Wallio",
                                                   "Nikita Petrov",
                                                   "Shawn Tabrizi",
                                                   "Randy Buehler",
                                                   "Samuel Hriljac",
                                                   "Shawn Yu",
                                                   "Tim Wilder",
                                                   "Tyler Marklyn"
                                                 )
                                                 GROUP BY t.tournament_id, p.player_id

                                                 UNION ALL SELECT
                                                             p.name as player_name,
                                                             d.name as deck_name,
                                                             t.date_played as date_played
                                                           FROM players p
                                                             INNER JOIN matches m
                                                               ON p.player_id = m.player_2
                                                             INNER JOIN decks d
                                                               ON d.deck_id = m.player_2_deck
                                                             INNER JOIN tournaments t
                                                               ON m.tournament_id = t.tournament_id
                                                           WHERE p.name IN (
                                                             "Adam Prusa",
                                                             "Alex Staver",
                                                             "Andy Stowell",
                                                             "Benjamin Richards",
                                                             "Brad Rutherford",
                                                             "Brian Bennett",
                                                             "Daniel Goh",
                                                             "Elston Cloy",
                                                             "Jeremy Edwards",
                                                             "Jordan Aisaka",
                                                             "Garth Brewe",
                                                             "Greg Mitchell",
                                                             "James Johnson",
                                                             "Martin Goldman-kirst",
                                                             "Matthew Staver",
                                                             "Michael Wallio",
                                                             "Miles Wallio",
                                                             "Nikita Petrov",
                                                             "Shawn Tabrizi",
                                                             "Randy Buehler",
                                                             "Samuel Hriljac",
                                                             "Shawn Yu",
                                                             "Tim Wilder",
                                                             "Tyler Marklyn"
                                                           )
                                                           GROUP BY t.tournament_id, p.player_id
)
GROUP BY player_name, date_played
ORDER BY player_name, date_played
