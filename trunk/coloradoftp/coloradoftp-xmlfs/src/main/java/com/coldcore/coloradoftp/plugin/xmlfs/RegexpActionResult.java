package com.coldcore.coloradoftp.plugin.xmlfs;

/**
 * Result when checking against regular expressions for certain actions.
 * This is used when method (A) checks the argument agains all regular expeessions till the
 * first match or returns a default value. If there are multiple methods calling method (A)
 * with different set of regular expressions then the method (A) must communicate back if
 * any regular expression has matched the argument to prevent the rest of the methods from
 * execution or to continue if the method (A) could not match the argument.
 */
public enum RegexpActionResult {
  ALLOW_NO_MATCH,  //action is allowed but regular expession did not match the argument
  FORBID_NO_MATCH, //action is forbidden but regular expession did not match the argument
  ALLOW_MATCH,     //action is allowed and regular expession matched the argument
  FORBID_MATCH     //action is forbidden and regular expession matched the argument
}