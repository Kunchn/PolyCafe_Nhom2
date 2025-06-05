/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package poly.cafe;

import javax.swing.SwingUtilities;
import poly.cafe.ui.PolyCafeJFrame;

/**
 *
 * @author hungp
 */
public class PolyCafe {

    public static void main(String[] args) {
        // Chạy giao diện Swing trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PolyCafeJFrame().setVisible(true); // Tạo và hiển thị cửa sổ chính
            }
        });
    }
}
