package de.unibi.agbi.biodwh2.procedures.utils;

/**
 * Provides math utility methods
 */
public class MathUtils {

    /**
     * Recursively calculates the factorial of a given number
     */
    public static int factorial(int number) {
        if(number == 0) {
            return 1;
        } else {
            return number * factorial(number - 1);
        }
    }

}
