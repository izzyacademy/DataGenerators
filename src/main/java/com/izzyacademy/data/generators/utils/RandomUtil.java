package com.izzyacademy.data.generators.utils;

public class RandomUtil {

    /**
     * Generates values with the min value inclusive and the max exclusive
     *
     * @param min the Minimum number (inclusive)
     * @param max the Maximum number (excluded)
     *
     * @return
     */
    public static int getRandomNumber(int min, int max) {

        // Returns a double value with a positive sign, greater than or equal to 0.0 and less than 1.0
        final double randomNumber = Math.random();

        // generates values with the min value inclusive and the max exclusive
        final int number = (int) (randomNumber * (max - min + 1) + min);

        // We dont want max to ever be returned
        if (number == max) {
            return Math.max(min, max-1);
        }

        return number;
    }
}
