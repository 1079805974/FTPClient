package test;

import java.io.File;
import java.text.ParseException;

public class test {
    public static void main(String args[]) throws ParseException {
        System.out.println("/htdocs/css".split("/")[0]);
        File file = new File("F:\\projects");
        System.out.println(file.isDirectory());
    }
}
