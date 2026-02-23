package domain;

/**
 * FlagStatus used to indicate if a record of an event has been updated since it was read.
 */
public enum FlagStatus {
  DIRTY,
  CLEAN
}