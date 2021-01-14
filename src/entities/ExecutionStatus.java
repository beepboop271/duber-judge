package entities;

public enum ExecutionStatus {
  // pending
  PENDING,

  // internal error
  INTERNAL_ERROR,

  // illegal submission
  UNKNOWN_LANGUAGE,
  ILLEGAL_CODE,

  // fails to setup
  COMPILE_ERROR,
  
  // fails to provide correct output
  INVALID_RETURN,
  WRONG_ANSWER,
  TIME_LIMIT_EXCEEDED,
  MEMORY_LIMIT_EXCEEDED,
  RUNTIME_ERROR,

  // successful
  ALL_CLEAR
}
