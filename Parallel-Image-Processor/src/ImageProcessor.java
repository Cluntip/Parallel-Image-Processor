import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ImageProcessor {

    public static void processAll(String studentId) {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        File[] folders = new File("extracted").listFiles(File::isDirectory);
        new File("output").mkdir();

        for (File folder : folders) {
            List<File> images = getImagesRecursively(folder);
            File outputSub = new File("output", folder.getName());
            outputSub.mkdirs();

            for (File image : images) {
                File relativePath = new File(outputSub, image.getName());
                executor.submit(() -> processImage(image, relativePath, studentId));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ignored) {}
    }

    private static List<File> getImagesRecursively(File dir) {
        List<File> imageFiles = new ArrayList<>();
        File[] all = dir.listFiles();
        if (all == null) return imageFiles;

        for (File file : all) {
            if (file.isDirectory()) {
                imageFiles.addAll(getImagesRecursively(file));
            } else if (file.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png)")) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    private static void processImage(File input, File output, String studentId) {
        try {
            BufferedImage original = ImageIO.read(input);
            if (original == null) {
                System.out.println("❌ Skipping (invalid image): " + input.getName());
                return;
            }

            BufferedImage gray = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = gray.createGraphics();
            g2d.drawImage(original, 0, 0, 256, 256, null);

            // Watermark
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(studentId);
            int textHeight = fm.getHeight();
            g2d.drawString(studentId, 256 - textWidth - 10, 256 - textHeight + 10);
            g2d.dispose();

            String format = output.getName().endsWith(".png") ? "png" : "jpg";
            ImageIO.write(gray, format, output);
            System.out.println("✅ Processed: " + output.getName());
        } catch (IOException e) {
            System.out.println("❌ Failed: " + input.getName());
        }
    }
}
