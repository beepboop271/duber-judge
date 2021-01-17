package entities;

public enum ExecutionStatus {

  PENDING,

  // the judge has encountered an error
  INTERNAL_ERROR,

  // illegal submission
  UNKNOWN_LANGUAGE,
  ILLEGAL_CODE,

  COMPILE_ERROR,

  // fails to provide correct output
  MEMORY_LIMIT_EXCEEDED,
  OUTPUT_LIMIT_EXCEEDED,
  TIME_LIMIT_EXCEEDED,
  INVALID_RETURN,
  WRONG_ANSWER,

  // untested due to failure of previous testcases in the same batch
  SKIPPED,

  ALL_CLEAR

}
