package stocktracker;

import java.io.*;
import java.nio.file.*;
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

        System.out.println("pom.xml exists: " + FileManager.fileExists("pom.xml"));
        System.out.println("b.txt exists: " + FileManager.fileExists("b.txt"));
        System.out.println(readLines(".gitignore"));
    }

    public static void writeLine(String dest, String writeLine, boolean append) {
        try (Writer writer = new BufferedWriter(new FileWriter(dest, append))){
            writer.write(writeLine + "\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeList(String dest, List<String> writeList) {
        try (Writer writer = new BufferedWriter(new FileWriter(dest))) {
            for (String writeLine: writeList) {
                writer.write(writeLine + "\n");
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public static List<String> readLines(String dest) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dest))){
            String line;
            do {
                line = reader.readLine();
                lines.add(line);
            } while (line != null);
            lines = lines.subList(0, lines.size()-1);
        } catch (NoSuchFileException e) {
            throw new InvalidPathException("", "No file exists at " + dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static boolean fileExists(String dest) {
        File file = new File(dest);
        return file.isFile();
    }

    public static void copyFile(String source, String out) {
        try {
            Files.copy(Paths.get(source), Paths.get(out), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTempFiles(String dest)
    {
        File dir = new File(dest);
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            if (!child.getPath().startsWith("save_") && child.getPath().contains("_temp")) {
                try {
                    //Files.deleteIfExists(Paths.get(child.getPath()));
                    child.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteAllFiles(String dest)
    {
        File dir = new File(dest);
        File[] directoryListing = dir.listFiles();
        for (File child : directoryListing) {
            if (child.getPath().contains("_temp") || child.getPath().contains("money")) {
                try {
                    //Files.deleteIfExists(Paths.get(child.getPath()));
                    child.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
