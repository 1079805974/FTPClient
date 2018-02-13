package core;

import utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RemoteFileInfo {
    private static final String DEFAULTHOUR = "8:00";
    //这个写法非常不负责任，把文件和文件夹混为一谈！但是时间紧迫！
    public boolean isFolder = false;
    private Date updateDate;
    private String filename;
    private String dir;
    private String size;

    public static RemoteFileInfo parse(String fileInfoLine, String dir) throws ParseException {
        RemoteFileInfo fileInfo = new RemoteFileInfo();
        //这个写法非常不负责任，把文件和文件夹混为一谈！
        if (fileInfoLine.startsWith("d")) {
            fileInfo.isFolder = true;
        }
        String splitArray[] = new String[9];
        for (int i = 0; i < 8; i++) {
            int firstBlack = fileInfoLine.indexOf(" ");
            splitArray[i] = fileInfoLine.substring(0, firstBlack);
            fileInfoLine = fileInfoLine.substring(firstBlack + 1).trim();
        }
        splitArray[8] = fileInfoLine;
        Date fileDate = parseToDate(splitArray[5], splitArray[6], splitArray[7]);
        fileInfo.setUpdateDate(fileDate);
        fileInfo.setDir(dir);
        fileInfo.setSize(splitArray[4]);
        fileInfo.setFilename(splitArray[8]);
        return fileInfo;
    }

    private static Date parseToDate(String month, String day, String yearOrHour) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar now = Calendar.getInstance();
        int thisYear = now.get(Calendar.YEAR);
        Date fileDate;
        String year, hour;
        month = Utils.monthToNumber(month) + "";
        if (!isYear(yearOrHour)) {
            hour = yearOrHour;
            year = thisYear + "";
            fileDate = formatter.parse(year + "-" + month + "-" + day + " " + hour);
            if (!hasArrived(fileDate)) {
                year = (thisYear - 1) + "";
                fileDate = formatter.parse(year + "-" + month + "-" + day + " " + hour);
            }
        } else {
            year = yearOrHour;
            hour = DEFAULTHOUR;
            fileDate = formatter.parse(year + "-" + month + "-" + day + " " + hour);
        }
        return fileDate;
    }

    private static boolean isYear(String date) {
        return !date.contains(":");
    }

    private static boolean hasArrived(Date date) {
        Calendar now = Calendar.getInstance();
        return now.getTime().compareTo(date) == -1;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String toString() {
        return "文件名：" + filename + "\n" +
                "所在目录：" + dir + "\n" +
                "文件大小：" + size + "\n" +
                "修改日期：" + updateDate + "\n";
    }
}
