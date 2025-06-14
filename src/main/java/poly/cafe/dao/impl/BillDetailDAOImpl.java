package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.entity.BillDetail;
import poly.cafe.util.XJdbc;

public class BillDetailDAOImpl implements BillDetailDAO {

    // Các câu lệnh SQL cho bảng BillDetails
    String createSql = "INSERT INTO BillDetails(BillId, DrinkId, UnitPrice, Discount, Quantity) VALUES(?, ?, ?, ?, ?)";
    String updateSql = "UPDATE BillDetails SET BillId=?, DrinkId=?, UnitPrice=?, Discount=?, Quantity=? WHERE Id=?";
    String deleteSql = "DELETE FROM BillDetails WHERE Id=?";
    String findAllSql = "SELECT * FROM BillDetails";
    String findByIdSql = "SELECT * FROM BillDetails WHERE Id=?";
    String findByBillIdSql = "SELECT * FROM BillDetails WHERE BillId = ?";

    // Phương thức private để chuyển đổi ResultSet thành đối tượng BillDetail
    private BillDetail mapResultSetToBillDetail(ResultSet rs) throws SQLException {
        return BillDetail.builder()
                .id(rs.getLong("Id"))
                .billId(rs.getLong("BillId"))
                .drinkId(rs.getString("DrinkId"))
                .unitPrice(rs.getDouble("UnitPrice"))
                .discount(rs.getInt("Discount")) // discount là int
                .quantity(rs.getInt("Quantity"))
                .build();
    }

    @Override
    public BillDetail create(BillDetail entity) {
        XJdbc.executeUpdate(createSql,
                entity.getBillId(),
                entity.getDrinkId(),
                entity.getUnitPrice(),
                entity.getDiscount(),
                entity.getQuantity());
        // Tương tự Bill, ID là tự tăng và XJdbc.executeUpdate không trả về nó.
        // Thường thì chi tiết hóa đơn không cần lấy lại ID ngay lập tức.
        return entity;
    }

    @Override
    public void update(BillDetail entity) {
        XJdbc.executeUpdate(updateSql,
                entity.getBillId(),
                entity.getDrinkId(),
                entity.getUnitPrice(),
                entity.getDiscount(),
                entity.getQuantity(),
                entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<BillDetail> findAll() {
        return this.selectBySql(findAllSql);
    }

    @Override
    public BillDetail findById(Long id) {
        List<BillDetail> list = this.selectBySql(findByIdSql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<BillDetail> findByBillId(Long billId) {
        return this.selectBySql(findByBillIdSql, billId);
    }
    
    // Phương thức trợ giúp để tránh lặp code truy vấn
    private List<BillDetail> selectBySql(String sql, Object... args) {
        List<BillDetail> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                list.add(mapResultSetToBillDetail(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn BillDetail: " + e.getMessage());
        }
        return list;
    }
}
