import java.io.*;
import java.util.concurrent.*;
import java.util.zip.*;

public class Extractor {

    public static void extractAll() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        File[] zips = new File("downloads").listFiles((dir, name) -> name.endsWith(".zip"));
        new File("extracted").mkdir();

        for (File zip : zips) {
            executor.submit(() -> extract(zip));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ignored) {}
    }

    private static void extract(File zipFile) {
        File outputDir = new File("extracted", zipFile.getName().replace(".zip", ""));
        outputDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
            System.out.println("✅ Extracted: " + zipFile.getName());
        } catch (IOException e) {
            System.out.println("❌ Error extracting: " + zipFile.getName());
        }
    }
}
