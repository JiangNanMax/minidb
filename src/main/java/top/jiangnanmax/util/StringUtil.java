package top.jiangnanmax.util;

public class StringUtil {

    public static final String BANNER = " /$$      /$$ /$$$$$$ /$$   /$$ /$$$$$$ /$$$$$$$  /$$$$$$$ \n" +
            "| $$$    /$$$|_  $$_/| $$$ | $$|_  $$_/| $$__  $$| $$__  $$\n" +
            "| $$$$  /$$$$  | $$  | $$$$| $$  | $$  | $$  \\ $$| $$  \\ $$\n" +
            "| $$ $$/$$ $$  | $$  | $$ $$ $$  | $$  | $$  | $$| $$$$$$$ \n" +
            "| $$  $$$| $$  | $$  | $$  $$$$  | $$  | $$  | $$| $$__  $$\n" +
            "| $$\\  $ | $$  | $$  | $$\\  $$$  | $$  | $$  | $$| $$  \\ $$\n" +
            "| $$ \\/  | $$ /$$$$$$| $$ \\  $$ /$$$$$$| $$$$$$$/| $$$$$$$/\n" +
            "|__/     |__/|______/|__/  \\__/|______/|_______/ |_______/ ";

    public static final String USAGE = "USAGE:\r\n" +
                                            "put $(key) $(value)\r\n" +
                                            "get $(key)\r\n" +
                                            "del $(key)\r\n" +
                                            "quit \r\n";

    public static String getCommand(String str) {
        return str.trim().split(" ")[0];
    }

    public static String getKey(String str) {
        String[] arr = str.trim().split(" ");
        if (arr.length != 2) {
            return null;
        }
        return arr[1];
    }

    public static String[] getKV(String str) {
        String[] arr = str.trim().split(" ");
        if (arr.length != 3) {
            return null;
        }
        return new String[] {arr[1], arr[2]};
    }

}
