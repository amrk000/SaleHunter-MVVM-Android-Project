package api.software.salehunter.util;

import java.util.regex.Pattern;

public final class TextFieldValidator {
    //Patterns
    private static final Pattern userNameRegex = Pattern.compile("((\\w|\\s|\\.|-)+){2,}");
    private static final Pattern emailRegex = Pattern.compile("(\\.*\\w+)+@([a-z]+|[A-Z]+)(\\.com|\\.sa)(\\.([a-z]+|[A-Z]+)+)?");
    private static final Pattern passwordRegex = Pattern.compile("(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}");

    //Length Ranges
    public static final int NO_MIN_VALUE = 0;
    public static final int NO_MAX_VALUE = -1;

    public static final int USERNAME_MIN = 2;
    public static final int USERNAME_MAX = 32;

    public static final int PASSWORD_MIN = 8;

    public static boolean isValidUsername(String username){
        return userNameRegex.matcher(username).matches();
    }

    public static boolean isValidEmail(String email){
        return emailRegex.matcher(email).matches();
    }

    public static boolean isValidPassword(String password){
        return passwordRegex.matcher(password).matches();
    }

    public static boolean inLengthRange(String input, int min, int max){
        if(max == -1) return input.length() >= min;
        return input.length() >= min && input.length() <= max;
    }

    public static boolean outLengthRange(String input, int min, int max){
        return !inLengthRange(input, min, max);
    }

}
