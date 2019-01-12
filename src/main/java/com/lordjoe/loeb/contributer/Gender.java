package com.lordjoe.loeb.contributer;

/**
 * com.lordjoe.loeb.contributer.Gender
 * User: Steve
 * Date: 4/23/2018
 */
public enum Gender {
    Male,Female,Unknown;

    public static   Gender fromString(String s)   {
        try {
            return Gender.valueOf(s);
        } catch (IllegalArgumentException e) {
            return Unknown;

        }
    }

}
