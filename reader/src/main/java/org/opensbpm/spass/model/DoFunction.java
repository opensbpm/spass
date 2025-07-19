package org.opensbpm.spass.model;

/**
 * Specifications or descriptions for Do-Functions describe in detail what the subject carrier is supposed to do in an according state.
 * The default DoFunction 1: present the surrounding execution environment with the given exit choices/conditions and receive choice of one exit option --&gt; define its Condition to be fulfilled in order to go to the next according state.
 * The default DoFunction 2: execute automatic rule evaluation (see DoTransitionCondition)
 * More specialized Do-Function Specifications may contain Data mappings denoting what of a subjects internal local Data can and should be:
 * a) read: in order to simply see it or in order to send it of to an external function (e.g. a web service)
 * b) write: in order to write incoming Data from e.g. a web Service or user input, to the local data fault
 *
 * @see http://www.i2pm.net/standard-pass-ont#DoFunction
 */
public interface DoFunction extends FunctionSpecification {

    interface Mutable extends DoFunction, FunctionSpecification.Mutable {

    }
}
