-- Meaning that it selects the highest submission for each problem for each user
SELECT submissions.*
  FROM submissions
    INNER JOIN (
      SELECT submissions.problem_id AS problem_id, MAX(submissions.score) AS highest_score
        FROM submissions
          INNER JOIN problems ON submissions.problem_id = problems.id
      WHERE submissions.user_id = ? AND problems.problem_type = 'PRACTICE'
      GROUP BY submissions.problem_id
      ORDER BY highest_score DESC
      LIMIT %s OFFSET %s
    ) AS a
          ON submissions.problem_id = a.problem_id
          AND submissions.score = a.highest_score;


--active contests
SELECT * FROM contests
WHERE user_id = ? AND status = 'ONGOING';


--get leaderboard of contest
SELECT * FROM contest_sessions
  WHERE contest_id = ?
  ORDER BY score DESC
  LIMIT %s OFFSET %s;


--get leaderboard of problem
SELECT submissions.*
  FROM submissions
    INNER JOIN (
      SELECT user_id, MAX(score) AS highest_score
        FROM submissions
        WHERE problem_id = ?
        GROUP BY user_id
        ORDER BY highest_score DESC
        LIMIT %s OFFSET %s
    ) a ON submissions.user_id = a.user_id
          AND submissions.score = a.highest_score;



