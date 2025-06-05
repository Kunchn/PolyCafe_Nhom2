/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import poly.cafe.util.XDialog; // Giả sử XDialog đã có và chứa phương thức confirm

/**
 * Interface điều khiển cho chức năng đăng nhập.
 */
public interface LoginController {

    /**
     * Xử lý khi cửa sổ đăng nhập được mở (ví dụ: căn giữa màn hình).
     */
    void open();

    /**
     * Xử lý logic khi người dùng nhấn nút "Đăng nhập".
     */
    void login();

    /**
     * Xử lý khi người dùng muốn thoát khỏi cửa sổ đăng nhập.
     * Có thể thoát toàn bộ ứng dụng.
     */
    default void exit() {
        if (XDialog.confirm(null, "Bạn muốn kết thúc?")) { // [cite: 191]
            System.exit(0);
        }
    }
}
