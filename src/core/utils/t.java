package core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class t {

    public static void main(String args[]) {

        Matcher m = Pattern.compile("\\{(\\w+): ([^}]+)\\}").matcher("/index/{thierry: \\d+}/{marina: \\w+}");
        while(m.find()) {
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }

    }

}
