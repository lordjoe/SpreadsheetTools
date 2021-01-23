package com.lordjoe.utilities;

import java.util.*;

/**
 * com.lordjoe.utilities.SubstitutionCode
 * User: Steve
 * Date: 11/22/20
 */
public class SubstitutionCode {
    public static final Random RND = new Random();
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SHIFT_ALPHABET = "UVWXYZABCDEFGHIJKLMNOPQRST";

    public static final String randomSubstitution()
    {
        StringBuilder sb = new StringBuilder();


        Set<Character> unused = new HashSet<>() ;
        char[] chars = ALPHABET.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            unused.add(aChar) ;
          }
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            char c = (char)('A' + RND.nextInt(26));
            if(c == aChar)
                continue;
            if(unused.contains(c)) {
                sb.append(c);
                unused.remove(c);
            }
        }

          return sb.toString();
    }
    private final Map<Character,Character> substitutions = new HashMap<>();

    public String makeSubstitution(String s) {
        StringBuilder sb = new StringBuilder();
         char[] chars = s.toUpperCase().toCharArray();
        for (int i = 0; i <  chars.length; i++) {
            char aChar = chars[i];
            Character o = substitutions.get(aChar);
            if(o != null)
                 sb.append(o);
            else
                sb.append(aChar);
        }
        return sb.toString();
    }

    public SubstitutionCode() {
        this( randomSubstitution());
    }

    public SubstitutionCode(String s) {
        char[] chars = ALPHABET.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            substitutions.put(aChar,s.charAt(i)) ;
        }
    }

    public static final String CLUE =  "the answer will require you to crack a simple letter substitution code. every letter is substituted" +
            " for a different letter. can you find what the real letters are?" +
            " When you do so go to the ship and look for the next clue.";


    public static void main(String[] args) {
        String s = new SubstitutionCode(SHIFT_ALPHABET).makeSubstitution(CLUE);
        System.out.println(s);
    }

}
