package amrk000.salehunter.util;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amrk000.salehunter.R;
import amrk000.salehunter.model.SettingsItemModel;

public final class AppSettingsManager extends Application {
    //App Themes Keys
    public static final int THEME_AUTO = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES;

    //Settings List Positions
    public static final int SETTINGS_LIST_LANGUAGE = 0;

    //App Languages Keys
    public static final String LANGUAGE_AUTO = "auto";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_ARABIC = "ar";
    public static final String[] LANGUAGES = {LANGUAGE_AUTO, LANGUAGE_ENGLISH, LANGUAGE_ARABIC};

    public static ArrayList<SettingsItemModel> getSettingsList(Context context){
        ArrayList<SettingsItemModel> settingsList = new ArrayList<>();
        settingsList.add(SETTINGS_LIST_LANGUAGE , new SettingsItemModel(context.getString(R.string.language),getLanguageName(context)));
        return settingsList;
    }

    public static int getTheme(Context context) {
        return SharedPrefManager.get(context).getTheme();
    }

    public static boolean isDarkModeEnabled(Context context) {
        return getTheme(context) == THEME_DARK;
    }

    public static void setTheme(Context context, int theme) {
        SharedPrefManager.get(context).setTheme(theme);
    }

    public static void setThemeAuto(Context context) {
        SharedPrefManager.get(context).setTheme(THEME_AUTO);
    }

    public static void setThemeLight(Context context) {
        SharedPrefManager.get(context).setTheme(THEME_LIGHT);
    }

    public static void setThemeDark(Context context) {
        SharedPrefManager.get(context).setTheme(THEME_DARK);
    }

    public static String getLanguageKey(Context context) {
        return SharedPrefManager.get(context).getLanguage();
    }

    public static int getLanguageIndex(Context context){
        String language = getLanguageKey(context);
        for(int i=0; i<LANGUAGES.length; i++) if(language.equals(LANGUAGES[i])) return i;
        return 0;
    }

    public static String getLanguageName(Context context){
        String language = getLanguageKey(context);
        if (language.equals(LANGUAGES[0])) return context.getString(R.string.auto);
        else if (language.equals(LANGUAGES[1])) return context.getString(R.string.english);
        else if (language.equals(LANGUAGES[2])) return context.getString(R.string.arabic);

        return "";
    }

    public static boolean isLanguageSystemDefault(Context context) {
        return getLanguageKey(context).equals(LANGUAGE_AUTO);
    }

    public static void setLanguage(Context context, int index) {
        SharedPrefManager.get(context).setLanguage(LANGUAGES[index]);
    }

    public static void setLanguage(Context context, String language) {
        SharedPrefManager.get(context).setLanguage(language);
    }

    public static List<String> getLanguagesList(Context context){
        return Arrays.asList(context.getResources().getStringArray(R.array.languages));
    }

}
