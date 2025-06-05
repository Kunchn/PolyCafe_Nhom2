/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.util;

import poly.cafe.entity.User; // Import lớp User

/**
 * Lớp tiện ích để duy trì và chia sẻ thông tin người dùng đăng nhập.
 */
public class XAuth {

    /**
     * Đối tượng User chứa thông tin người dùng hiện tại đang đăng nhập.
     * Khởi tạo với giá trị mặc định (sẽ được thay thế khi đăng nhập).
     */
    public static User user = User.builder()
            .username("trump")
            .password("123")
            .enabled(true)
            .manager(true)
            .fullname("Nguyễn Văn Tèo")
            .photo("trump.png") // Đảm bảo bạn có file 'trump.png' hoặc đổi tên file khác
            .build();

    // Lưu ý: biến 'user' này sẽ được cập nhật lại sau khi đăng nhập thành công.
    // Việc khởi tạo giá trị mặc định ở đây chủ yếu giúp
    // tránh NullPointerException và thuận tiện cho việc kiểm thử ban đầu.

    /**
     * Xóa thông tin người dùng khi đăng xuất.
     */
    public static void clear() {
        XAuth.user = null;
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa.
     * @return true nếu đã đăng nhập.
     */
    public static boolean isLogin() {
        return XAuth.user != null;
    }

    /**
     * Kiểm tra xem người dùng có phải là quản lý hay không.
     * @return true nếu là quản lý và đã đăng nhập.
     */
    public static boolean isManager() {
        return XAuth.isLogin() && XAuth.user.isManager();
    }
}