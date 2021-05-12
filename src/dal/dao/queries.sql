--netsed problems
SELECT problems.*, batches.*, testcases.*
  FROM testcases
    INNER JOIN (
      SELECT problems.*, batches.*
      FROM problems INNER JOIN batches ON problems.id = batches.problem_id
      WHERE problems.id = ?
    ) ON testcases.batch_id = batches.id
  ORDER BY batches.id;

--unresolved clarification requests
SELECT clarifications.*
  FROM clarifications INNER JOIN problems
    ON clarifications.problem_id = problems.id
    WHERE problems.creator_id = ?
ORDER BY clarifications.created_at ASC
LIMIT %s OFFSET %s;


--delete testcase runs by problem
DELETE FROM testcase_runs
  FROM testcase_runs
    INNER JOIN submissions
    ON testcase_runs.submission_id = submissions.id
    WHERE submissions.problem_id = ?;

--updates contest sessions that are over
UPDATE contest_sessions
SET status = 'OVER'
WHERE (datetime('now') - created_at < duration)
  FROM (
    SELECT duration_minutes AS duration
    FROM contest_sessions INNER JOIN contests
    ON contest_session.id = contests.id
  );


UPDATE submissions
SET
  status = ?,
  score = ?,
  run_duration_millis = ?,
  memory_usage_b = ?
WHERE id = ?;