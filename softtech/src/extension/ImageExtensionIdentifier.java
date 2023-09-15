package extension;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageExtensionIdentifier {

    public static String getImageExtension(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {   /* 경로를 체크하여 파일이 존재하는지 확인 */
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] buffer = new byte[8];
                fis.read(buffer);

                // 파일 시그니처로 이미지 형식 식별
                if (isJPEG(buffer)) {
                    return "jpg";
                } else if (isPNG(buffer)) {
                    return "png";
                } else if (isGIF(buffer)) {
                    return "gif";
                } else if (isPDF(buffer)) {
                    return "pdf";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static boolean isJPEG(byte[] buffer) {
        return buffer.length >= 2 && buffer[0] == (byte) 0xFF && buffer[1] == (byte) 0xD8;
    }

    private static boolean isPNG(byte[] buffer) {
        return buffer.length >= 8 && buffer[0] == (byte) 0x89 && buffer[1] == (byte) 0x50 &&
                buffer[2] == (byte) 0x4E && buffer[3] == (byte) 0x47 && buffer[4] == (byte) 0x0D &&
                buffer[5] == (byte) 0x0A && buffer[6] == (byte) 0x1A && buffer[7] == (byte) 0x0A;
    }

    private static boolean isGIF(byte[] buffer) {
        return buffer.length >= 6 && buffer[0] == (byte) 0x47 && buffer[1] == (byte) 0x49 &&
                buffer[2] == (byte) 0x46 && buffer[3] == (byte) 0x38 &&
                (buffer[4] == (byte) 0x37 || buffer[4] == (byte) 0x39) && buffer[5] == (byte) 0x61;
    }

    private static boolean isPDF(byte[] buffer) {
        return buffer.length >= 5 && buffer[0] == (byte) 0x25 && buffer[1] == (byte) 0x50 &&
                buffer[2] == (byte) 0x44 && buffer[3] == (byte) 0x46 && buffer[4] == (byte) 0x2D;
    }
}