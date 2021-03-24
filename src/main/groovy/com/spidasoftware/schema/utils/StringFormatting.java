/*
 * ©2009-2015 SPIDAWEB LLC
 */

package com.spidasoftware.schema.utils;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

public class StringFormatting {

    /**
     * Convert all upper case string to a capitalized string with underscores removed.
     *
     * @return the formatted string
     */
    public static String makeReadableString(String enumerableString) {
        char ch;       // One of the characters in str.
        char prevCh = '.';   // The character that comes before ch in the string.
        char[] chars = new char[enumerableString.length()];
        for (int i = 0; i < enumerableString.length(); i++) {
            ch = enumerableString.charAt(i);
            if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
                chars[i] = Character.toUpperCase(ch);
            } else if (Character.isLetter(ch)) {
                chars[i] = Character.toLowerCase(ch);
            } else if (ch == '_') {
                chars[i] = ' ';
            } else {
                chars[i] = (ch);
            }
            prevCh = ch;
        }
        return new String(chars);
    }

    public static String makeReadableString(Enum enumerable) {
        return makeReadableString(enumerable.name());
    }

    public static String[] makeReadableString(Enum[] enumerables) {
        String[] stringArray = new String[enumerables.length];
        for (int i = 0; i < enumerables.length; i++) {
            Enum enumerable = enumerables[i];
            stringArray[i] = makeReadableString(enumerable);
        }
        return stringArray;
    }

    /**
     * Convert to an all upper case string with underscores.
     *
     * @return the formatted string
     */
    public static String makeEnumerableString(String readableString) {
        char ch;       // One of the characters in str.
        char[] chars = new char[readableString.length()];
        for (int i = 0; i < readableString.length(); i++) {
            ch = readableString.charAt(i);
            if (Character.isLetter(ch)) {
                chars[i] = Character.toUpperCase(ch);
            } else if (ch == ' ') {
                chars[i] = '_';
            } else if (ch == '-') {
                chars[i] = '_';
            } else {
                chars[i] = (ch);
            }
        }
        return new String(chars);
    }

    /**
     * this_test_string becomes thisTestString
     */
    public static String convertUnderscoreToCamel(String stringWithUnderscores) {
        String[] stringArray = stringWithUnderscores.split("_");
        StringBuilder camelString = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            String string = stringArray[i];
            if (i > 0) {
                string = StringUtils.capitalize(string);
            }
            camelString.append(string);
        }
        return camelString.toString();
    }

    /**
     * thisTestString or ThisTestString becomes This Test String
     */
    public static String camelToReadable(String camelCase) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        String clean = StringUtils.join(words, " ");
        return WordUtils.capitalize(clean);
    }

    /**
     * This Test String or this test string becomes thisTestString
     */
    public static String readableToCamel(String readable) {
        String capitalized = WordUtils.capitalize(readable);
        String noSpaces = capitalized.replaceAll(" ", "");
        return WordUtils.uncapitalize(noSpaces);
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
}
