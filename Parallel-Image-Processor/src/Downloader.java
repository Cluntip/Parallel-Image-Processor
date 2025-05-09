import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;

public class Downloader {

    public static void downloadAll(List<String> urls) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        new File("downloads").mkdir();

        for (String url : urls) {
            executor.submit(() -> download(url));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ignored) {}
    }

    private static void download(String fileURL) {
        String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        Path outputPath = Paths.get("downloads", fileName);
        for (int attempt = 0; attempt <= 1; attempt++) {
            try (InputStream in = new URL(fileURL).openStream()) {
                Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Downloaded: " + fileName);
                break;
            } catch (IOException e) {
                System.out.println("⚠️ Retry " + fileName);
            }
        }
    }
}
