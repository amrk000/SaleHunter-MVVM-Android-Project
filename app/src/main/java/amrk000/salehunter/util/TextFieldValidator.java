package amrk000.salehunter.util;

import java.util.regex.Pattern;

public final class TextFieldValidator {
    //Patterns
    private static final Pattern nameRegex = Pattern.compile("((\\w|\\s|\\.|-)+){2,}");
    private static final Pattern userNameIdRegex = Pattern.compile("(([A-Z]|[a-z]|[0-9]|\\.|_)+){5,50}");
    private static final Pattern emailRegex = Pattern.compile("(\\.*\\w+)+@([a-z]+|[A-Z]+)(\\.com|\\.sa)(\\.([a-z]+|[A-Z]+)+)?");
    private static final Pattern passwordRegex = Pattern.compile("(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}");
    private static final Pattern storeWebsiteRegex = Pattern.compile("(http:\\/\\/|https:\\/\\/)(www\\.|store\\.|shop\\.|marketplace\\.)?([A-Z]|[a-z]|[0-9]|-){2,}(\\.([A-Z]|[a-z]){2,7})(\\.\\w{2,3})?\\/?");

    //Length Ranges
    public static final int NO_MIN_VALUE = 0;
    public static final int NO_MAX_VALUE = -1;

    public static final int USERNAME_MIN = 2;
    public static final int USERNAME_MAX = 32;

    public static final int PASSWORD_MIN = 8;

    public static final int STORENAME_MIN = 2;
    public static final int STORENAME_MAX = 32;

    public static final int PRODUCTNAME_MIN = 8;
    public static final int PRODUCTNAME_MAX = 64;

    public static final int CATEGORY_MIN = 4;
    public static final int CATEGORY_MAX = 24;

    public static final int ADDRESS_MIN = 16;
    public static final int ADDRESS_MAX = 128;

    public static final int DESCRIPTION_MIN = 25;
    public static final int DESCRIPTION_MAX = 128;

    public static final int PHONE_MIN = 11;
    public static final int PHONE_MAX = 11; //Egypt Max Number Length

    public static final int BRAND_MIN = 3;
    public static final int BRAND_MAX = 16;

    public static boolean isValidName(String username){
        return nameRegex.matcher(username).matches();
    }

    public static boolean isValidUserNameId(String userNameId){
        return userNameIdRegex.matcher(userNameId).matches();
    }

    public static boolean isValidEmail(String email){
        return emailRegex.matcher(email).matches();
    }

    public static boolean isValidPassword(String password){
        return passwordRegex.matcher(password).matches();
    }

    public static boolean isValidStoreName(String storeName){
        return nameRegex.matcher(storeName).matches();
    }

    public static boolean isValidStoreWebsite(String storeWebsite){
        return storeWebsiteRegex.matcher(storeWebsite).matches();
    }

    public static boolean isValidProductName(String productName){
        return nameRegex.matcher(productName).matches();
    }

    public static boolean inLengthRange(String input, int min, int max){
        if(max == -1) return input.length() >= min;
        return input.length() >= min && input.length() <= max;
    }

    public static boolean outLengthRange(String input, int min, int max){
        return !inLengthRange(input, min, max);
    }

}
