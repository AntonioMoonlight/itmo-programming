package common;

import java.io.Serializable;
import java.util.Arrays;

public enum MusicGenre implements Comparable<MusicGenre>, Serializable {

    POP,
    SOUL,
    MATH_ROCK;

    public static String allowed = Arrays.toString(values());
}
