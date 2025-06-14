/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import poly.cafe.ui.manager.DrinkManagerJDialog;
import poly.cafe.util.XDialog;

/**
 *
 * @author hungp
 */
public interface PolyCafeController {
    /**
     * Hiển thị cửa sổ chào
     * Hiển thị cửa sổ đăng nhập
     * Hiển thị thông tin user đăng nhập
     * Disable/Enable các thành phần tùy thuộc vào vai trò đăng nhập
     */
    void init(); // [cite: 126]

    default void exit() { // [cite: 127]
        // Giả sử XDialog đã được bạn thêm vào poly.cafe.util
        // và có phương thức confirm(Component parent, String message)
        // Nếu XDialog.confirm yêu cầu một parent component, bạn có thể cần truyền this (JFrame) vào
        // hoặc sửa lại XDialog cho phù hợp.
        // Hiện tại, XDialog.confirm("Bạn muốn kết thúc?") là từ tài liệu.
        if (XDialog.confirm(null, "Bạn muốn kết thúc?")) { // [cite: 127]
            System.exit(0); // [cite: 127]
        }
    }
    
    default void showSalesView() {
        new poly.cafe.ui.sales.SalesView().setVisible(true);
    }

    default void showJDialog(JDialog dialog) { // [cite: 127]
        dialog.setLocationRelativeTo(null); // [cite: 127]
        dialog.setVisible(true); // [cite: 127]
    }

    default void showWelcomeJDialog(JFrame frame) { // [cite: 127]
        // Chúng ta sẽ sử dụng WelcomeJDialog đã tạo
        this.showJDialog(new WelcomeJDialog(frame, true)); // [cite: 127]
    }

    default void showLoginJDialog(JFrame frame) { // [cite: 128]
        this.showJDialog(new LoginJDialog(frame, true));
    }

    default void showChangePasswordJDialog(JFrame frame) { // [cite: 128]
        // Ví dụ: this.showJDialog(new ChangePasswordJDialog(frame, true)); // [cite: 128]
    }

    default void showSalesJDialog(JFrame frame) { // [cite: 129]
        // Ví dụ: this.showJDialog(new SalesJDialog(frame, true)); // [cite: 129]
    }

    default void showHistoryJDialog(JFrame frame) { // [cite: 129]
        // Ví dụ: this.showJDialog(new HistoryJDialog(frame, true)); // [cite: 129]
    }

    // Các phương thức show...Dialog cho các chức năng quản lý
    // Person 1 chỉ cần khai báo, việc tạo các JDialog này (CategoryManagerJDialog, DrinkManagerJDialog,...)
    // và hoàn thiện các default method này sẽ do các thành viên khác hoặc ở các bước sau.
    default void showDrinkManagerJDialog(JFrame frame) { // [cite: 130]
        this.showJDialog(new DrinkManagerJDialog(frame, true));
    }

    default void showCategoryManagerJDialog(JFrame frame) { // [cite: 131]
        // Ví dụ: this.showJDialog(new CategoryManagerJDialog(frame, true)); // [cite: 131]
    }

    default void showCardManagerJDialog(JFrame frame) { // [cite: 131]
        // Ví dụ: this.showJDialog(new CardManagerJDialog(frame, true)); // [cite: 131]
    }

    default void showBillManagerJDialog(JFrame frame) { // [cite: 132]
        // Ví dụ: this.showJDialog(new BillManagerJDialog(frame, true)); // [cite: 132]
    }

    default void showUserManagerJDialog(JFrame frame) { // [cite: 132]
        // Ví dụ: this.showJDialog(new UserManagerJDialog(frame, true)); // [cite: 132]
    }

    default void showRevenueManagerJDialog(JFrame frame) { 
    }
}
