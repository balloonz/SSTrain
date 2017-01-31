package com.fteams.sstrain.util;

public class SongUtils {
    public final static Long NOTE_TYPE_NORMAL = 1l;
    public final static Long NOTE_TYPE_HOLD = 2l;

    public final static Integer NOTE_SYNC_OFF = 0;
    public final static Integer NOTE_SYNC_ON = 1;

    public final static Long NOTE_NO_SWIPE = 0L;
    public final static Long NOTE_SWIPE_LEFT = 1L;
    public final static Long NOTE_SWIPE_RIGHT = 2L;


    public final static Integer SORTING_MODE_SONG_NAME = 0;
    public final static Integer SORTING_MODE_SONG_ID = 1;
    public final static Integer SORTING_MODE_ATTRIBUTE = 2;
    public final static Integer SORTING_MODE_MASPLUS_LV = 3;
    public final static Integer SORTING_MODE_MAS_LV = 4;
    public final static Integer SORTING_MODE_PRO_LV = 5;
    public final static Integer SORTING_MODE_REG_LV = 6;
    public final static Integer SORTING_MODE_DEB_LV = 7;
    public final static String[] sortModes = {"Song Name","Song ID","Attribute","Master+ Lv","Master Lv","Pro Lv","Regular Lv","Debut Lv"};

    // no longer in use:
    public final static Integer SORTING_MODE_FILE_NAME = 99;

    public final static Integer SORTING_MODE_ASCENDING = 0;
    public final static Integer SORTING_MODE_DESCENDING = 1;

    public final static Integer SYNC_MODE_1 = 0;
    public final static Integer SYNC_MODE_2 = 1;
    public final static Integer SYNC_MODE_3 = 2;
    public final static Integer SYNC_DISABLED = 3;

    public final static Integer FALL_MODE_CONST = 0;
    public final static Integer FALL_MODE_PARAB = 1;

    public final static Integer GAME_MODE_NORMAL = 0;
    public final static Integer GAME_MODE_ABREPEAT = 1;

    public final static String[] fallModes = {"Vertical", "Parabolic"};
    public final static String[] syncModes = {"Default", "Constant Sync", "Initial Sync", "Disabled"};
    public final static Long[] noteSpeeds = {1800L, 1680L, 1560L, 1440L, 1320L, 1200L, 1050L, 900L, 750L, 600L, 450L};

    public final static Double[] overallDiffPerfect = {79.5, 73.5, 67.5, 61.5, 56.5, 49.5, 43.5, 37.5, 31.5, 25.5, 19.5}; // -6
    public final static Double[] overallDiffGreat = {139.5, 131.5, 123.5, 115.5, 107.5, 99.5, 91.5, 83.5, 75.5, 67.5, 59.5}; // -8
    public final static Double[] overallDiffNice = {199.5, 189.5, 179.5, 169.5, 159.5, 149.5, 139.5, 129.5, 119.5, 109.5, 99.5}; // - 10
    public final static Double[] overallDiffBad = {249.5, 237.5, 225.5, 213.5, 201.5, 189.5, 177.5, 165.5, 153.5, 141.5, 129.5};// - 12

    public final static Integer[] difficultyLvRange = {1,10,15,20,29,999}; // Debut:1-9, Reg:10-14, Pro: 15-19, Mas: 20-28, Mas+: 29+

    public static Long getSpeedFromConfig(Integer noteSpeed) {
        return noteSpeeds[noteSpeed];
    }

    public final static String[] attributes = {"Cute", "Cool", "Passion", "ALL"};

    public static String getAttribute(Long attribute) {
        if (attribute == null) {
            return "Unknown";
        }
        if (attribute == 0) {
            return "TUTORIAL";
        }
        return attributes[(int) (attribute - 1)];
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
