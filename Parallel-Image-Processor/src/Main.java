import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> urls = List.of(
                "https://github.com/yavuzceliker/sample-images/archive/refs/heads/main.zip"
        );

        String studentId = "Amr Hamada 22101177";

        Downloader.downloadAll(urls);
        Extractor.extractAll();
        ImageProcessor.processAll(studentId);

    }
}