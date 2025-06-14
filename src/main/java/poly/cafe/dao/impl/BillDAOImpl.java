package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.BillDAO;
import poly.cafe.entity.Bill;
import poly.cafe.util.XJdbc;

public class BillDAOImpl implements BillDAO {

    // Các câu lệnh SQL cho bảng Bills
    String createSql = "INSERT INTO Bills(Username, CardId, Checkin, Checkout, Status) VALUES(?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Bills SET Username=?, CardId=?, Checkin=?, Checkout=?, Status=? WHERE Id=?";
    String deleteSql = "DELETE FROM Bills WHERE Id=?";
    String findAllSql = "SELECT * FROM Bills";
    String findByIdSql = "SELECT * FROM Bills WHERE Id=?";
    String findByStatusSql = "SELECT * FROM Bills WHERE Status = ?";

    // Phương thức private để chuyển đổi ResultSet thành đối tượng Bill
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        return Bill.builder()
                .id(rs.getLong("Id"))
                .username(rs.getString("Username"))
                .cardId(rs.getInt("CardId"))
                .checkin(rs.getTimestamp("Checkin")) // Dùng getTimestamp cho DATETIME
                .checkout(rs.getTimestamp("Checkout")) // Dùng getTimestamp cho DATETIME
                .status(rs.getInt("Status"))
                .build();
    }

    @Override
    public Bill create(Bill entity) {
        // Bảng Bills có cột Id là IDENTITY (tự tăng), nên chúng ta không thêm Id vào.
        // Tuy nhiên, XJdbc.executeUpdate không trả về khóa tự tăng.
        // Một cách tiếp cận là dùng XJdbc.getValue để lấy Id vừa tạo.
        // Hoặc, sửa createSql để không bao gồm Id và sau khi insert, truy vấn lại để lấy Bill đầy đủ.
        // Để đơn giản, chúng ta sẽ insert và giả định việc lấy lại Id được xử lý ở tầng cao hơn nếu cần.
        // Hoặc chúng ta có thể sửa createSql để trả về ID.
        // Ví dụ với SQL Server: INSERT INTO...; SELECT SCOPE_IDENTITY();
        // XJdbc hiện tại không hỗ trợ việc này, nên ta sẽ dùng XJdbc.getValue.

        String createAndGetIdSql = "INSERT INTO Bills(Username, CardId, Checkin, Checkout, Status) VALUES(?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();";
        
        try {
            // XJdbc.getValue sẽ thực thi và trả về giá trị từ cột đầu tiên của bản ghi đầu tiên,
            // trong trường hợp này là ID vừa được tạo bởi SCOPE_IDENTITY().
            Object newIdObj = XJdbc.getValue(createAndGetIdSql,
                    entity.getUsername(),
                    entity.getCardId(),
                    entity.getCheckin(),
                    entity.getCheckout(),
                    entity.getStatus());
            
            // Chuyển đổi ID về kiểu Long và cập nhật lại cho entity
            if (newIdObj instanceof Number) {
                entity.setId(((Number) newIdObj).longValue());
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo Bill: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void update(Bill entity) {
        XJdbc.executeUpdate(updateSql,
                entity.getUsername(),
                entity.getCardId(),
                entity.getCheckin(),
                entity.getCheckout(),
                entity.getStatus(),
                entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Bill> findAll() {
        return this.selectBySql(findAllSql);
    }

    @Override
    public Bill findById(Long id) {
        List<Bill> list = this.selectBySql(findByIdSql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Bill> findByStatus(int status) {
        return this.selectBySql(findByStatusSql, status);
    }

    // Phương thức trợ giúp để tránh lặp code truy vấn
    private List<Bill> selectBySql(String sql, Object... args) {
        List<Bill> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn Bill: " + e.getMessage());
        }
        return list;
    }
}
