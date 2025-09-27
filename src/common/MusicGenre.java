package common;

import java.util.Arrays;

public enum MusicGenre implements Comparable<MusicGenre> {

    POP,
    SOUL,
    MATH_ROCK;

    public static String allowed = Arrays.toString(values());
}
