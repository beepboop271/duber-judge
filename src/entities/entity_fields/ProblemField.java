package entities.entity_fields;

/**
 * [description]
 * <p>
 * Created on 2021.01.03.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ProblemField implements EntityField {
  PROBLEM_TYPE,
  CATEGORY,
  CREATOR_ID,
  CREATED_AT,
  LAST_MODIFIED_AT,
  TITLE,
  DESCRIPTION,
  POINTS,
  TIME_LIMIT_MILLIS,
  MEMORY_LIMIT_KB,
  OUTPUT_LIMIT_KB,
  NUM_SUBMISSIONS,
  CLEARED_SUBMISSIONS,

  // contest problems only
  SUBMISSIONS_LIMIT,
  CONTEST_ID,

  // practice problems only
  EDITORIAL,


  //admin
  PUBLISHING_STATE,


}
