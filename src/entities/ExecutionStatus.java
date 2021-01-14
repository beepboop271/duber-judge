package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ExecutionStatus {
  PENDING,

  INTERNAL_ERROR,

  // illegal submission
  UNKNOWN_LANGUAGE,
  ILLEGAL_CODE,

  COMPILE_ERROR,

  // fails to provide correct output
  INVALID_RETURN,
  WRONG_ANSWER,
  TIME_LIMIT_EXCEEDED,
  MEMORY_LIMIT_EXCEEDED,
  RUNTIME_ERROR,

  ALL_CLEAR
}
