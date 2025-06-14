/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package poly.cafe.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.dao.impl.DrinkDAOImpl;
import poly.cafe.entity.Bill;
import poly.cafe.entity.BillDetail;
import poly.cafe.entity.Drink;
import poly.cafe.util.XAuth;
import poly.cafe.util.XDialog;
import poly.cafe.util.XIcon;

/**
 *
 * @author hungp
 */
public class PolyCafeJFrame extends javax.swing.JFrame implements PolyCafeController{

    /**
     * Creates new form PolyCafeJFrame
     */
    DrinkDAO drinkDAO = new DrinkDAOImpl();
    BillDAO billDAO = new BillDAOImpl();
    BillDetailDAO billDetailDAO = new BillDetailDAOImpl();

    // Danh sách để lưu trữ các món hàng trong hóa đơn đang được tạo (hóa đơn tạm thời)
    List<BillDetail> currentOrderDetails = new ArrayList<>();

    // Hóa đơn đang được chọn từ bảng "Hóa đơn chờ"
    Bill selectedBill = null;
    
    // --- KẾT THÚC PHẦN KHAI BÁO MỚI ---
    public PolyCafeJFrame() {
        initComponents();
        this.init();    
    }
    
    /**
 * Tải danh sách sản phẩm từ CSDL và hiển thị động lên pnlProductListDisplay.
 */
    /**
 * Tạo một JPanel đại diện cho một ô sản phẩm để hiển thị.
 * @param drink Đối tượng Drink chứa thông tin sản phẩm.
 * @return Một JPanel đã được thiết kế hoàn chỉnh.
 */
    private javax.swing.JPanel createProductPanel(final Drink drink) {
        // Panel chính cho một sản phẩm
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS)); // Xếp dọc
        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel.setPreferredSize(new java.awt.Dimension(180, 240)); // Kích thước cố định cho mỗi ô
        panel.setMaximumSize(new java.awt.Dimension(180, 240));
        panel.setBackground(java.awt.Color.WHITE);

        // JLabel hiển thị ảnh
        javax.swing.JLabel lblImage = new javax.swing.JLabel();
        lblImage.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblImage.setPreferredSize(new java.awt.Dimension(160, 120));
        ImageIcon icon = XIcon.getIcon(drink.getImage()); // Lấy icon từ tên file ảnh
        if (icon != null) {
            // Scale ảnh cho vừa với JLabel
            java.awt.Image scaledImage = icon.getImage().getScaledInstance(160, 120, java.awt.Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaledImage));
        } else {
            lblImage.setText("No Image");
        }

        // JLabel hiển thị tên (dùng html để tự xuống dòng nếu tên quá dài)
        javax.swing.JLabel lblName = new javax.swing.JLabel("<html><center>" + drink.getName() + "</center></html>");
        lblName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // JLabel hiển thị giá
        javax.swing.JLabel lblPrice = new javax.swing.JLabel(String.format("%,.0f VNĐ", drink.getUnitPrice()));
        lblPrice.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblPrice.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblPrice.setForeground(java.awt.Color.RED);

        // Panel nhỏ chứa các control số lượng
        javax.swing.JPanel qtyPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 2));
        qtyPanel.setOpaque(false); // Làm trong suốt để lấy màu nền của panel cha
        javax.swing.JButton btnMinus = new javax.swing.JButton("-");
        javax.swing.JTextField txtQty = new javax.swing.JTextField("1", 3); // Ô số lượng, rộng 3 ký tự
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setEditable(false);
        javax.swing.JButton btnPlus = new javax.swing.JButton("+");

        // Xử lý sự kiện cho nút trừ (-)
        btnMinus.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(txtQty.getText());
                if (qty > 1) { // Số lượng không thể nhỏ hơn 1
                    txtQty.setText(String.valueOf(qty - 1));
                }
            } catch (NumberFormatException ex) {
                txtQty.setText("1");
            }
        });

        // Xử lý sự kiện cho nút cộng (+)
        btnPlus.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(txtQty.getText());
                txtQty.setText(String.valueOf(qty + 1)); // Tăng số lượng
            } catch (NumberFormatException ex) {
                txtQty.setText("1");
            }
        });

        // Thêm các nút +/- và ô số lượng vào panel nhỏ
        qtyPanel.add(btnMinus);
        qtyPanel.add(txtQty);
        qtyPanel.add(btnPlus);

        // Nút "Thêm vào hóa đơn" (kích hoạt logic thêm sản phẩm)
        // Thay vì dùng nút riêng, chúng ta sẽ làm cho toàn bộ panel có thể click được.
        // Hoặc, đơn giản hơn là dùng sự kiện của nút "+"
        // Khi người dùng nhấn "+", nó sẽ vừa tăng số lượng, vừa có thể được hiểu là thêm vào giỏ hàng.
        // Để rõ ràng, chúng ta sẽ xử lý việc thêm vào giỏ hàng khi người dùng click vào chính panel sản phẩm.
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Đây là nơi logic thêm sản phẩm vào hóa đơn hiện tại sẽ được gọi
                int quantity = Integer.parseInt(txtQty.getText());
                addProductToCurrentOrder(drink, quantity); // Sẽ triển khai phương thức này ở bước sau
                txtQty.setText("1"); // Reset số lượng về 1 sau khi thêm
            }
        });

        // Thêm các component vào panel chính theo thứ tự
        panel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5))); // Khoảng đệm
        panel.add(lblImage);
        panel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
        panel.add(lblName);
        panel.add(lblPrice);
        panel.add(qtyPanel);
        panel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));

        return panel;
    }


    private void fillProductList() {
        // Đặt layout cho panel chứa sản phẩm. GridLayout là một lựa chọn tốt để tạo lưới.
        // (số hàng, số cột, khoảng cách ngang, khoảng cách dọc)
        // Đặt số hàng là 0 để nó tự động thêm hàng mới khi cần.
        pnlProductListDisplay.setLayout(new java.awt.GridLayout(0, 4, 10, 10));

        pnlProductListDisplay.removeAll(); // Xóa các sản phẩm mẫu đã thiết kế (nếu có)

        try {
            List<Drink> allDrinks = drinkDAO.findAll(); // Lấy tất cả đồ uống từ CSDL
            for (Drink drink : allDrinks) {
                // Chỉ hiển thị các sản phẩm có sẵn để bán
                if (drink.isAvailable()) {
                    // Gọi một phương thức trợ giúp để tạo một panel cho mỗi sản phẩm
                    javax.swing.JPanel productPanel = createProductPanel(drink);
                    pnlProductListDisplay.add(productPanel);
                }
            }
        } catch (Exception e) {
            XDialog.alert("Lỗi tải danh sách sản phẩm!", "Lỗi");
            e.printStackTrace();
        }

        // Cập nhật lại giao diện để hiển thị các panel sản phẩm mới
        pnlProductListDisplay.revalidate();
        pnlProductListDisplay.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMenuLeft = new javax.swing.JPanel();
        lblFullname = new javax.swing.JLabel();
        lblPhoto = new javax.swing.JLabel();
        btnBanHang = new javax.swing.JButton();
        btnTrangChu = new javax.swing.JButton();
        btnQuanLySP = new javax.swing.JButton();
        btnQuanLyHD = new javax.swing.JButton();
        btnQuanLyNV = new javax.swing.JButton();
        btnThongKe = new javax.swing.JButton();
        pnlCurrentBillInfo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCurrentBillDetails = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblCurrentBillId = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblCurrentBillTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pnlProductListDisplay = new javax.swing.JPanel();
        pnlProductItem1 = new javax.swing.JPanel();
        lblProductItem1Image = new javax.swing.JLabel();
        lblProductItem1Name = new javax.swing.JLabel();
        lblProductItem1Price = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnProductItem1Minus = new javax.swing.JButton();
        btnProductItem1Plus = new javax.swing.JButton();
        txtProductItem1Quantity = new javax.swing.JLabel();
        pnlProductItem2 = new javax.swing.JPanel();
        lblProductItem2Image = new javax.swing.JLabel();
        lblProductItem2Price = new javax.swing.JLabel();
        lblProductItem2Name = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnProductItem1Minus1 = new javax.swing.JButton();
        btnProductItem1Plus1 = new javax.swing.JButton();
        txtProductItem1Quantity1 = new javax.swing.JLabel();
        pnlProductItem4 = new javax.swing.JPanel();
        lblProductItem4Image = new javax.swing.JLabel();
        lblProductItem4Name = new javax.swing.JLabel();
        lblProductItem4Price = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnProductItem1Minus4 = new javax.swing.JButton();
        btnProductItem1Plus4 = new javax.swing.JButton();
        txtProductItem1Quantity4 = new javax.swing.JLabel();
        pnlProductItem3 = new javax.swing.JPanel();
        lblProductItem3Image = new javax.swing.JLabel();
        lblProductItem3Price = new javax.swing.JLabel();
        lblProductItem3Name = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnProductItem1Minus2 = new javax.swing.JButton();
        btnProductItem1Plus2 = new javax.swing.JButton();
        txtProductItem1Quantity2 = new javax.swing.JLabel();
        btnCreateNewBill = new javax.swing.JButton();
        pnlPendingBills = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPendingBills = new javax.swing.JTable();
        pnlPaymentInfo = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JLabel();
        txtCustomerPhone = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDiscountCode = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtFinalTotal = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnProcessPayment = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poly Cafe");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlMenuLeft.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlMenuLeft.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblFullname.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblFullname.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFullname.setText("Admin");
        pnlMenuLeft.add(lblFullname, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, -1, -1));

        lblPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/poly/cafe/icons/trump-small.png"))); // NOI18N
        lblPhoto.setText("Ảnh User");
        pnlMenuLeft.add(lblPhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, 80));

        btnBanHang.setText("Bán hàng");
        btnBanHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBanHangActionPerformed(evt);
            }
        });
        pnlMenuLeft.add(btnBanHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 170, 40));

        btnTrangChu.setText("Trang chủ");
        pnlMenuLeft.add(btnTrangChu, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 170, 40));

        btnQuanLySP.setText("Quản lý SP");
        pnlMenuLeft.add(btnQuanLySP, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 170, 40));

        btnQuanLyHD.setText("Quản lý HĐ");
        pnlMenuLeft.add(btnQuanLyHD, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 170, 40));

        btnQuanLyNV.setText("Quản lý NV");
        pnlMenuLeft.add(btnQuanLyNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 170, 40));

        btnThongKe.setText("Thống kê");
        pnlMenuLeft.add(btnThongKe, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 170, 40));

        getContentPane().add(pnlMenuLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 640));

        pnlCurrentBillInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tblCurrentBillDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên", "Giá", "Số lượng"
            }
        ));
        jScrollPane1.setViewportView(tblCurrentBillDetails);

        jLabel1.setText("Mã HĐ:");

        lblCurrentBillId.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Thành tiền:");

        lblCurrentBillTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Thông tin hóa đơn");

        javax.swing.GroupLayout pnlCurrentBillInfoLayout = new javax.swing.GroupLayout(pnlCurrentBillInfo);
        pnlCurrentBillInfo.setLayout(pnlCurrentBillInfoLayout);
        pnlCurrentBillInfoLayout.setHorizontalGroup(
            pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                .addGroup(pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCurrentBillId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCurrentBillTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                                .addGroup(pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        pnlCurrentBillInfoLayout.setVerticalGroup(
            pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCurrentBillInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCurrentBillInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCurrentBillId, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCurrentBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(pnlCurrentBillInfoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        getContentPane().add(pnlCurrentBillInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 500, 290));

        pnlProductListDisplay.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlProductItem1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem1Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/poly/cafe/icons/banhmi2.png"))); // NOI18N
        lblProductItem1Image.setText("jLabel6");
        lblProductItem1Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem1Name.setText("Bánh mì");

        lblProductItem1Price.setText("20.000 vnd");

        btnProductItem1Minus.setText("-");
        btnProductItem1Minus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1MinusActionPerformed(evt);
            }
        });

        btnProductItem1Plus.setText("+");
        btnProductItem1Plus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1PlusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProductItem1Minus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProductItem1Plus))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProductItem1Minus)
                    .addComponent(btnProductItem1Plus))
                .addGap(0, 46, Short.MAX_VALUE))
        );

        txtProductItem1Quantity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlProductItem1Layout = new javax.swing.GroupLayout(pnlProductItem1);
        pnlProductItem1.setLayout(pnlProductItem1Layout);
        pnlProductItem1Layout.setHorizontalGroup(
            pnlProductItem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductItem1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductItem1Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProductItem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductItem1Name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProductItem1Price, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addGroup(pnlProductItem1Layout.createSequentialGroup()
                        .addComponent(txtProductItem1Quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlProductItem1Layout.setVerticalGroup(
            pnlProductItem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProductItem1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProductItem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProductItem1Layout.createSequentialGroup()
                        .addComponent(lblProductItem1Name, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProductItem1Price)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlProductItem1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlProductItem1Layout.createSequentialGroup()
                                .addComponent(txtProductItem1Quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlProductItem1Layout.createSequentialGroup()
                        .addComponent(lblProductItem1Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlProductListDisplay.add(pnlProductItem1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 230, 130));

        pnlProductItem2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem2Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/poly/cafe/icons/nuocchanh.png"))); // NOI18N
        lblProductItem2Image.setText("jLabel6");
        lblProductItem2Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem2Price.setText("15.000 vnd");

        lblProductItem2Name.setText("Nước chanh");

        btnProductItem1Minus1.setText("-");
        btnProductItem1Minus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Minus1ActionPerformed(evt);
            }
        });

        btnProductItem1Plus1.setText("+");
        btnProductItem1Plus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Plus1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProductItem1Minus1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProductItem1Plus1))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProductItem1Minus1)
                    .addComponent(btnProductItem1Plus1))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        txtProductItem1Quantity1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlProductItem2Layout = new javax.swing.GroupLayout(pnlProductItem2);
        pnlProductItem2.setLayout(pnlProductItem2Layout);
        pnlProductItem2Layout.setHorizontalGroup(
            pnlProductItem2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductItem2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductItem2Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProductItem2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductItem2Name, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(lblProductItem2Price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlProductItem2Layout.createSequentialGroup()
                        .addComponent(txtProductItem1Quantity1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlProductItem2Layout.setVerticalGroup(
            pnlProductItem2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductItem2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProductItem2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProductItem2Layout.createSequentialGroup()
                        .addComponent(lblProductItem2Name)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProductItem2Price)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlProductItem2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlProductItem2Layout.createSequentialGroup()
                                .addComponent(txtProductItem1Quantity1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlProductItem2Layout.createSequentialGroup()
                        .addComponent(lblProductItem2Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 38, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlProductListDisplay.add(pnlProductItem2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 240, 130));

        pnlProductItem4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem4Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/poly/cafe/icons/caphe1.png"))); // NOI18N
        lblProductItem4Image.setText("jLabel6");
        lblProductItem4Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem4Name.setText("Cà phê");

        lblProductItem4Price.setText("25.000 vn");

        btnProductItem1Minus4.setText("-");
        btnProductItem1Minus4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Minus4ActionPerformed(evt);
            }
        });

        btnProductItem1Plus4.setText("+");
        btnProductItem1Plus4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Plus4ActionPerformed(evt);
            }
        });

        txtProductItem1Quantity4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(txtProductItem1Quantity4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProductItem1Minus4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProductItem1Plus4))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProductItem1Quantity4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnProductItem1Minus4)
                        .addComponent(btnProductItem1Plus4)))
                .addGap(0, 30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlProductItem4Layout = new javax.swing.GroupLayout(pnlProductItem4);
        pnlProductItem4.setLayout(pnlProductItem4Layout);
        pnlProductItem4Layout.setHorizontalGroup(
            pnlProductItem4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductItem4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductItem4Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProductItem4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductItem4Name, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(lblProductItem4Price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlProductItem4Layout.setVerticalGroup(
            pnlProductItem4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProductItem4Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnlProductItem4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProductItem4Layout.createSequentialGroup()
                        .addComponent(lblProductItem4Name)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProductItem4Price)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblProductItem4Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlProductListDisplay.add(pnlProductItem4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 240, 130));

        pnlProductItem3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem3Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/poly/cafe/icons/tradao1.png"))); // NOI18N
        lblProductItem3Image.setText("jLabel6");
        lblProductItem3Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblProductItem3Price.setText("15.000 vnd");

        lblProductItem3Name.setText("Trà đào");

        btnProductItem1Minus2.setText("-");
        btnProductItem1Minus2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Minus2ActionPerformed(evt);
            }
        });

        btnProductItem1Plus2.setText("+");
        btnProductItem1Plus2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductItem1Plus2ActionPerformed(evt);
            }
        });

        txtProductItem1Quantity2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(txtProductItem1Quantity2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProductItem1Minus2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProductItem1Plus2))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProductItem1Quantity2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnProductItem1Minus2)
                        .addComponent(btnProductItem1Plus2)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlProductItem3Layout = new javax.swing.GroupLayout(pnlProductItem3);
        pnlProductItem3.setLayout(pnlProductItem3Layout);
        pnlProductItem3Layout.setHorizontalGroup(
            pnlProductItem3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductItem3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductItem3Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProductItem3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProductItem3Name, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addComponent(lblProductItem3Price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlProductItem3Layout.setVerticalGroup(
            pnlProductItem3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProductItem3Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnlProductItem3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProductItem3Layout.createSequentialGroup()
                        .addComponent(lblProductItem3Image, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(25, Short.MAX_VALUE))
                    .addGroup(pnlProductItem3Layout.createSequentialGroup()
                        .addComponent(lblProductItem3Name)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProductItem3Price)
                        .addGap(12, 12, 12)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pnlProductListDisplay.add(pnlProductItem3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 230, 130));

        btnCreateNewBill.setText("Tạo Hóa Đơn");
        btnCreateNewBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewBillActionPerformed(evt);
            }
        });
        pnlProductListDisplay.add(btnCreateNewBill, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 0, -1, 30));

        getContentPane().add(pnlProductListDisplay, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 330, 470, 290));

        pnlPendingBills.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Danh sách hóa đơn chờ");

        tblPendingBills.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "HD1", "Chờ"}
            },
            new String [] {
                "STT", "Mã HD", "Trạng thái"
            }
        ));
        jScrollPane2.setViewportView(tblPendingBills);

        javax.swing.GroupLayout pnlPendingBillsLayout = new javax.swing.GroupLayout(pnlPendingBills);
        pnlPendingBills.setLayout(pnlPendingBillsLayout);
        pnlPendingBillsLayout.setHorizontalGroup(
            pnlPendingBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingBillsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPendingBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlPendingBillsLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 46, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlPendingBillsLayout.setVerticalGroup(
            pnlPendingBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPendingBillsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(pnlPendingBills, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, 260, 290));

        pnlPaymentInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Thông tin thanh toán");

        jLabel7.setText("Tên khách hàng: ");

        txtCustomerName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtCustomerPhone.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("SĐT:");

        txtDiscountCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setText("Mã giảm giá:");

        txtFinalTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("Tổng tiền:");

        btnProcessPayment.setText("Thanh Toán");

        javax.swing.GroupLayout pnlPaymentInfoLayout = new javax.swing.GroupLayout(pnlPaymentInfo);
        pnlPaymentInfo.setLayout(pnlPaymentInfoLayout);
        pnlPaymentInfoLayout.setHorizontalGroup(
            pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPaymentInfoLayout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFinalTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDiscountCode, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addComponent(txtCustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
            .addGroup(pnlPaymentInfoLayout.createSequentialGroup()
                .addGroup(pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPaymentInfoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(pnlPaymentInfoLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btnProcessPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPaymentInfoLayout.setVerticalGroup(
            pnlPaymentInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaymentInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDiscountCode, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFinalTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProcessPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        getContentPane().add(pnlPaymentInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 290, 290, 370));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Danh sách sản phẩm");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, -1, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBanHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBanHangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBanHangActionPerformed

    private void btnProductItem1MinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1MinusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1MinusActionPerformed

    private void btnProductItem1PlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1PlusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1PlusActionPerformed

    private void btnProductItem1Minus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Minus1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Minus1ActionPerformed

    private void btnProductItem1Plus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Plus1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Plus1ActionPerformed

    private void btnProductItem1Minus2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Minus2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Minus2ActionPerformed

    private void btnProductItem1Plus2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Plus2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Plus2ActionPerformed

    private void btnProductItem1Minus4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Minus4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Minus4ActionPerformed

    private void btnProductItem1Plus4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductItem1Plus4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductItem1Plus4ActionPerformed

    private void btnCreateNewBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateNewBillActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateNewBillActionPerformed
    
    private void addProductToCurrentOrder(Drink drink, int quantity) {
    for (BillDetail detail : currentOrderDetails) {
            if (detail.getDrinkId().equals(drink.getId())) {
                // Nếu đã có, chỉ cần cập nhật lại số lượng
                detail.setQuantity(detail.getQuantity() + quantity);
                updateCurrentOrderTable(); // Cập nhật lại bảng và thành tiền
                return; // Kết thúc phương thức
            }
        }

        // Nếu chưa có, tạo một BillDetail mới và thêm vào danh sách
        BillDetail newDetail = BillDetail.builder()
                .drinkId(drink.getId())
                .unitPrice(drink.getUnitPrice())
                .discount(drink.getDiscount()) // discount kiểu int
                .quantity(quantity)
                // billId sẽ được gán sau khi hóa đơn được tạo
                .build();

        currentOrderDetails.add(newDetail);
        updateCurrentOrderTable(); // Cập nhật lại bảng và thành tiền
    }

        /**
     * Cập nhật lại bảng tblCurrentBillDetails và tính tổng thành tiền
     * từ danh sách hóa đơn tạm thời currentOrderDetails.
     */
    private void updateCurrentOrderTable() {
        DefaultTableModel model = (DefaultTableModel) tblCurrentBillDetails.getModel();
        model.setRowCount(0); // Xóa tất cả các dòng cũ trong bảng

        double totalAmount = 0;

        for (BillDetail detail : currentOrderDetails) {
            // Cần lấy tên đồ uống từ DrinkDAO
            Drink drink = drinkDAO.findById(detail.getDrinkId());
            String drinkName = (drink != null) ? drink.getName() : "Sản phẩm không xác định";
            
            double price = detail.getUnitPrice();
            int quantity = detail.getQuantity();
            // Vì discount là int (ví dụ: 10 cho 10%), ta phải chia cho 100.0 khi tính toán
            double discountRate = (double) detail.getDiscount() / 100.0;
            
            double lineTotal = price * quantity * (1 - discountRate);
            totalAmount += lineTotal;

            // Thêm dòng mới vào bảng
            model.addRow(new Object[]{
                drinkName,
                String.format("%,.0f", price),
                quantity
            });
        }

        // Cập nhật lại tổng tiền trên giao diện
        lblCurrentBillTotal.setText(String.format("%,.0f VNĐ", totalAmount));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PolyCafeJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PolyCafeJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBanHang;
    private javax.swing.JButton btnCreateNewBill;
    private javax.swing.JButton btnProcessPayment;
    private javax.swing.JButton btnProductItem1Minus;
    private javax.swing.JButton btnProductItem1Minus1;
    private javax.swing.JButton btnProductItem1Minus2;
    private javax.swing.JButton btnProductItem1Minus4;
    private javax.swing.JButton btnProductItem1Plus;
    private javax.swing.JButton btnProductItem1Plus1;
    private javax.swing.JButton btnProductItem1Plus2;
    private javax.swing.JButton btnProductItem1Plus4;
    private javax.swing.JButton btnQuanLyHD;
    private javax.swing.JButton btnQuanLyNV;
    private javax.swing.JButton btnQuanLySP;
    private javax.swing.JButton btnThongKe;
    private javax.swing.JButton btnTrangChu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCurrentBillId;
    private javax.swing.JLabel lblCurrentBillTotal;
    private javax.swing.JLabel lblFullname;
    private javax.swing.JLabel lblPhoto;
    private javax.swing.JLabel lblProductItem1Image;
    private javax.swing.JLabel lblProductItem1Name;
    private javax.swing.JLabel lblProductItem1Price;
    private javax.swing.JLabel lblProductItem2Image;
    private javax.swing.JLabel lblProductItem2Name;
    private javax.swing.JLabel lblProductItem2Price;
    private javax.swing.JLabel lblProductItem3Image;
    private javax.swing.JLabel lblProductItem3Name;
    private javax.swing.JLabel lblProductItem3Price;
    private javax.swing.JLabel lblProductItem4Image;
    private javax.swing.JLabel lblProductItem4Name;
    private javax.swing.JLabel lblProductItem4Price;
    private javax.swing.JPanel pnlCurrentBillInfo;
    private javax.swing.JPanel pnlMenuLeft;
    private javax.swing.JPanel pnlPaymentInfo;
    private javax.swing.JPanel pnlPendingBills;
    private javax.swing.JPanel pnlProductItem1;
    private javax.swing.JPanel pnlProductItem2;
    private javax.swing.JPanel pnlProductItem3;
    private javax.swing.JPanel pnlProductItem4;
    private javax.swing.JPanel pnlProductListDisplay;
    private javax.swing.JTable tblCurrentBillDetails;
    private javax.swing.JTable tblPendingBills;
    private javax.swing.JLabel txtCustomerName;
    private javax.swing.JLabel txtCustomerPhone;
    private javax.swing.JLabel txtDiscountCode;
    private javax.swing.JLabel txtFinalTotal;
    private javax.swing.JLabel txtProductItem1Quantity;
    private javax.swing.JLabel txtProductItem1Quantity1;
    private javax.swing.JLabel txtProductItem1Quantity2;
    private javax.swing.JLabel txtProductItem1Quantity4;
    // End of variables declaration//GEN-END:variables

    // Đặt đoạn code này bên trong lớp PolyCafeJFrame.java của bạn

    @Override
    public void init() {
        // ----- BƯỚC 1: CÀI ĐẶT CƠ BẢN CHO CỬA SỔ -----
        // Đặt icon cho ứng dụng
        // Đảm bảo bạn có file 'logo_app.png' (hoặc tên tương ứng) trong thư mục resources/poly/cafe/icons/
        ImageIcon appIcon = XIcon.getIcon("logo_app.png");
        if (appIcon != null) {
            this.setIconImage(appIcon.getImage());
        }

        // Đặt JFrame ra giữa màn hình
        this.setLocationRelativeTo(null);

        // ----- BƯỚC 2: QUÁ TRÌNH CHÀO VÀ ĐĂNG NHẬP (MODAL) -----
        // Các dialog này là modal, nghĩa là code sẽ dừng ở mỗi dòng cho đến khi dialog tương ứng được đóng.
        this.showWelcomeJDialog(this); // Hiển thị màn hình chào
        this.showLoginJDialog(this);   // Sau khi màn hình chào đóng, hiển thị màn hình đăng nhập

        // ----- BƯỚC 3: XỬ LÝ SAU KHI ĐĂNG NHẬP -----
        // Code ở đây chỉ chạy sau khi màn hình đăng nhập đã đóng.
        if (XAuth.isLogin()) {
            // A. CẬP NHẬT GIAO DIỆN VỚI THÔNG TIN NGƯỜI DÙNG
            updateUserInfo();

            // B. PHÂN QUYỀN TRUY CẬP CHỨC NĂNG
            applyUserRoles();

            // C. TẢI DỮ LIỆU BAN ĐẦU CHO GIAO DIỆN BÁN HÀNG
            fillProductList();      // Tải và hiển thị danh sách sản phẩm
            fillPendingBills();     // Tải và hiển thị danh sách hóa đơn chờ
            clearCurrentOrder();    // Chuẩn bị cho hóa đơn mới

        } else {
            // Nếu người dùng đóng cửa sổ đăng nhập mà không đăng nhập
            XDialog.alert("Bạn chưa đăng nhập. Ứng dụng sẽ thoát.", "Lỗi");
            System.exit(0);
        }
    }

    /**
     * Cập nhật thông tin người dùng (ảnh đại diện, tên) trên menu trái.
     */
    private void updateUserInfo() {
        if (lblFullname != null) {
            lblFullname.setText(XAuth.user.getFullname());
        }
        if (lblPhoto != null) {
            // Dùng XIcon.setIcon để tự động scale ảnh cho vừa JLabel
            XIcon.setIcon(lblPhoto, XAuth.user.getPhoto());
        }
    }

    /**
     * Áp dụng phân quyền: Bật/tắt các nút chức năng dựa trên vai trò người dùng.
     */
    private void applyUserRoles() {
        boolean isManager = XAuth.isManager();
        // Các nút quản lý chỉ dành cho người có vai trò Manager
        if (btnQuanLySP != null) btnQuanLySP.setEnabled(isManager);
        if (btnQuanLyHD != null) btnQuanLyHD.setEnabled(isManager);
        if (btnQuanLyNV != null) btnQuanLyNV.setEnabled(isManager);
        if (btnThongKe != null) btnThongKe.setEnabled(isManager);

        // Các nút khác như "Trang chủ", "Bán hàng" có thể luôn được bật
        if (btnTrangChu != null) btnTrangChu.setEnabled(true);
        if (btnBanHang != null) btnBanHang.setEnabled(true);
    }

    /**
     * Tải danh sách các hóa đơn đang ở trạng thái chờ (status = 0) và hiển thị lên bảng tblPendingBills.
     */
    private void fillPendingBills() {
        DefaultTableModel model = (DefaultTableModel) tblPendingBills.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            // Giả sử trạng thái "Chờ" có giá trị là 0 trong CSDL
            List<Bill> pendingBills = billDAO.findByStatus(0); 
            int stt = 1;
            for (Bill bill : pendingBills) {
                model.addRow(new Object[]{
                    stt++,
                    "HD" + bill.getId(), // Hiển thị mã hóa đơn
                    "Chờ" // Trạng thái
                });
            }
        } catch (Exception e) {
            XDialog.alert("Lỗi tải danh sách hóa đơn chờ!", "Lỗi");
            e.printStackTrace();
        }
    }

    /**
     * Xóa trắng và reset khu vực thông tin hóa đơn hiện tại để chuẩn bị cho một đơn hàng mới.
     */
    private void clearCurrentOrder() {
        currentOrderDetails.clear(); // Xóa tất cả các món trong giỏ hàng tạm thời
        updateCurrentOrderTable();   // Cập nhật lại bảng chi tiết hóa đơn (sẽ trở nên trống)
        lblCurrentBillId.setText("..."); // Reset mã hóa đơn

        // Reset thông tin thanh toán
        txtCustomerName.setText("");
        txtCustomerPhone.setText("");
        txtDiscountCode.setText("");
        txtFinalTotal.setText("0 VNĐ");
    }

}
