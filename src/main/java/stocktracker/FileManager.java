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
        exampleList.add("asdasdasd");
        exampleList.add("daaddd");
        exampleList.add("st");

        FileManager.writeList("a.txt", exampleList);
        System.out.println(FileManager.fileExists("a.txt"));
        System.out.println(FileManager.fileExists("b.txt"));
    }

    public static void writeList(String dest, List<String> writeList) {
        boolean append = false;
        for (String writeLine: writeList) {
            writeLine(dest, writeLine, append);
            append = true;
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

    public static void writeLine(String dest, String writeLine, boolean append) {
        try {
            FileWriter writer = new FileWriter(dest, append);
            writer.write(writeLine + "\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newFile(String fileDest)
    {
        try {
            Files.newBufferedWriter(Paths.get(fileDest));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String dest) {
        File file = new File(dest);
        return file.isFile();
    }
}
