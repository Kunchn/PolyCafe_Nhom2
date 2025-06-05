package poly.cafe.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL; // Thêm import này
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class XIcon {
    /**
     * Đọc icon từ resource hoặc file
     * @param path đường dẫn file, đường dẫn resource hoặc tên resource
     * @return ImageIcon hoặc null nếu không tìm thấy
     */
    public static ImageIcon getIcon(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println("Lỗi XIcon: Đường dẫn icon rỗng hoặc null.");
            return null;
        }
        if (!path.contains("/") && !path.contains("\\")) { // resource name
            // Tự động thêm tiền tố nếu chỉ là tên file
            return XIcon.getIcon("/poly/cafe/icons/" + path);
        }
        if (path.startsWith("/")) { // resource path (đường dẫn tuyệt đối trong classpath)
            URL imgURL = XIcon.class.getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Lỗi XIcon: Không tìm thấy resource tại classpath: " + path);
                return null; // Trả về null để code gọi có thể xử lý
            }
        }
        // Nếu không phải resource path (không bắt đầu bằng /), coi như là đường dẫn file tuyệt đối hoặc tương đối trên hệ thống file
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            return new ImageIcon(path);
        } else {
            System.err.println("Lỗi XIcon: Không tìm thấy file tại đường dẫn hệ thống: " + path);
            return null;
        }
    }

    /**
     * Đọc icon theo kích thước
     * @param path đường dẫn file hoặc tài nguyên
     * @param width chiều rộng
     * @param height chiều cao
     * @return ImageIcon hoặc null nếu icon gốc không tìm thấy
     */
    public static ImageIcon getIcon(String path, int width, int height) {
        ImageIcon originalIcon = getIcon(path);
        if (originalIcon != null && originalIcon.getImage() != null) {
            Image image = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        }
        return null; // Trả về null nếu icon gốc không được tải
    }

    /**
     * Thay đổi icon của JLabel
     * @param label JLabel cần thay đổi
     * @param path đường dẫn file hoặc tài nguyên
     */
    public static void setIcon(JLabel label, String path) {
        if (label != null) {
            ImageIcon icon = XIcon.getIcon(path, label.getWidth(), label.getHeight());
            label.setIcon(icon); // Sẽ set null nếu icon không tải được
        }
    }

    /**
     * Thay đổi icon của JLabel
     * @param label JLabel cần thay đổi
     * @param file file icon
     */
    public static void setIcon(JLabel label, File file) {
        if (file != null) {
            XIcon.setIcon(label, file.getAbsolutePath());
        } else if (label != null) {
            label.setIcon(null);
        }
    }

    /**
     * Sao chép file vào thư mục với tên file mới là duy nhất
     * @param fromFile file cần sao chép
     * @param folder thư mục đích
     * @return File đã sao chép
     */
    public static File copyTo(File fromFile, String folder) {
        if (fromFile == null || !fromFile.exists()) {
            System.err.println("Lỗi copyTo: File nguồn không tồn tại hoặc null.");
            return null;
        }
        String fileExt = "";
        String fileName = fromFile.getName();
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            fileExt = fileName.substring(lastDot);
        }
        
        File toFile = new File(folder, XStr.getKey() + fileExt); // Đảm bảo XStr.getKey() hoạt động
        File parentDir = toFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return toFile;
        } catch (IOException ex) {
            System.err.println("Lỗi copyTo: " + ex.getMessage());
            // throw new RuntimeException(ex); // Cân nhắc không throw RuntimeException ở lớp tiện ích
            return null;
        }
    }

    public static File copyTo(File fromFile) {
        return copyTo(fromFile, "files"); // Mặc định lưu vào thư mục "files"
    }
}
