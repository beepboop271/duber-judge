package entities;

/**
 * An enum containing possible execution statuses of submissions or testcase runs.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ExecutionStatus {

  /** An internal error was encountered during judging. */
  INTERNAL_ERROR,

  // illegal submission
  /** The language used is not supported by the judge. */
  UNKNOWN_LANGUAGE,
  /** The program contains illegal code snippets. */
  ILLEGAL_CODE,

  /** The program failed to properly compile. */
  COMPILE_ERROR,

  // fails to provide correct output
  /** The program took too long to execute. */
  TIME_LIMIT_EXCEEDED,
  /** The program ran out of memory. */
  MEMORY_LIMIT_EXCEEDED,
  /** The program program outputted too much data. */
  OUTPUT_LIMIT_EXCEEDED,
  /** The program raised an exception or error. */
  INVALID_RETURN,
  /** The output produced was incorrect. */
  WRONG_ANSWER,

  /**
   * The program was untested due to failure of clearing previous testcases
   * in the same batch.
   */
  SKIPPED,

  /** The testcase run / submission passed all required testing. */
  ALL_CLEAR,

  PENDING
}
