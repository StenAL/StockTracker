package stocktracker;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void main(String[] args) {
        FileManager.test();
    }

    private static void test()
    {
        ArrayList<String> exampleList = new ArrayList<>();
        FileManager.writeLine("x.txt", "a", false);
        exampleList.add("asdasdasd");
        exampleList.add("daaddd");
        exampleList.add("st");

        FileManager.writeList("a.txt", exampleList);
        FileManager.deleteFiles("src\\main\\resources", "_temp");

        System.out.println("pom.xml exists: " + FileManager.fileExists("pom.xml"));
        System.out.println("b.txt exists: " + FileManager.fileExists("b.txt"));
        System.out.println(readLines(".gitignore"));
    }

    public static void writeList(String dest, List<String> writeList) {
        boolean append = false;
        for (String writeLine: writeList) {
            writeLine(dest, writeLine, append);
            append = true;
        }
    }

    public static void deleteFiles(String dest, String suffix)
    {
        File dir = new File(dest);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String fname = child.getName();
                int pos = fname.lastIndexOf(".");
                if (pos > 0) {
                    fname = fname.substring(0, pos);
                }
                if (fname.endsWith(suffix)) {
                    child.delete();
                }

            }
        }
    }

    public static void writeArray(String dest, Object[] writeArray) {
        boolean append = false;
        for (Object writeObject: writeArray) {
            String writeLine = writeObject.toString();
            writeLine(dest, writeLine, append);
            append = true;
        }
    }

    public static List<String> readLines(String dest) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            Files.lines(Paths.get(dest))
                    .forEach(lines::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLine(String dest, String writeLine, boolean append) {
        try {
            FileWriter writer = new FileWriter(dest, append);
            writer.write(writeLine + "\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String dest) {
        File file = new File(dest);
        return file.isFile();
    }
}
