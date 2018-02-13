package utils;

public class Utils {
    public static int monthToNumber(String month) {
        if (month.equals("Jan")) {
            return 1;
        }
        if (month.equals("Feb")) {
            return 2;
        }
        if (month.equals("Mar")) {
            return 3;
        }
        if (month.equals("Apr")) {
            return 4;
        }
        if (month.equals("May")) {
            return 5;
        }
        if (month.equals("Jun")) {
            return 6;
        }
        if (month.equals("Jul")) {
            return 7;
        }
        if (month.equals("Aug")) {
            return 8;
        }
        if (month.equals("Sep")) {
            return 9;
        }
        if (month.equals("Oct")) {
            return 10;
        }
        if (month.equals("Nov")) {
            return 11;
        }
        if (month.equals("Dec")) {
            return 12;
        }
        return 0;
    }

}
