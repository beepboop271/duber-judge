package entities;

/**
 * An enum containing the various execution statuses of submissions.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ExecutionStatus {
  /** The submission is waiting to be judged. */
  PENDING,

  /** An internal error was encountered during judging. */
  INTERNAL_ERROR,

  // illegal submission
  /** The language used is not accepted by the judge. */
  UNKNOWN_LANGUAGE,
  /** The submission contains illegal code snippets. */
  ILLEGAL_CODE,

  /** The submission failed to properly compile. */
  COMPILE_ERROR,

  // fails to provide correct output
  /** The submission raised an exception or error. */
  INVALID_RETURN,
  /** The output produced was incorrect. */
  WRONG_ANSWER,
  /** The submission took too long to execute. */
  TIME_LIMIT_EXCEEDED,
  /** The submission ran out of memory. */
  MEMORY_LIMIT_EXCEEDED,
  /** The submission caused a runtime exception to occur. */
  RUNTIME_ERROR,

  /** The submission passed all the tests. */
  ALL_CLEAR
}
