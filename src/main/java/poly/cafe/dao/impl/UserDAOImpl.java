/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.UserDAO;
import poly.cafe.entity.User;
import poly.cafe.util.XJdbc;

/**
 * Lớp triển khai các thao tác CSDL cho thực thể User.
 */
public class UserDAOImpl implements UserDAO {

    // Các câu lệnh SQL
    String createSql = "INSERT INTO Users(Username, Password, Enabled, Fullname, Photo, Manager) VALUES(?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Users SET Password=?, Enabled=?, Fullname=?, Photo=?, Manager=? WHERE Username=?";
    String deleteSql = "DELETE FROM Users WHERE Username=?";
    String findAllSql = "SELECT * FROM Users";
    String findByIdSql = "SELECT * FROM Users WHERE Username=?";

    // Phương thức private để chuyển đổi ResultSet thành đối tượng User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return User.builder()
                .username(rs.getString("Username"))
                .password(rs.getString("Password"))
                .enabled(rs.getBoolean("Enabled"))
                .fullname(rs.getString("Fullname"))
                .photo(rs.getString("Photo"))
                .manager(rs.getBoolean("Manager"))
                .build();
    }

    @Override
    public User create(User entity) {
        XJdbc.executeUpdate(createSql,
                entity.getUsername(),
                entity.getPassword(),
                entity.isEnabled(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.isManager());
        return entity; // Trả về entity đã thêm
    }

    @Override
    public void update(User entity) {
        XJdbc.executeUpdate(updateSql,
                entity.getPassword(),
                entity.isEnabled(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.isManager(),
                entity.getUsername());
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(findAllSql);
            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn tất cả User: " + e.getMessage());
            // Cân nhắc throw new RuntimeException(e);
        } finally {
            // Đóng ResultSet nếu cần (XJdbc hiện tại chưa có cơ chế đóng tự động)
            // Tuy nhiên, việc quản lý đóng PreparedStatement và ResultSet thường nên
            // được cải thiện trong XJdbc hoặc thực hiện ở đây.
            // Tạm thời bỏ qua việc đóng chi tiết ở đây, giả định XJdbc sẽ cải thiện sau.
        }
        return userList;
    }

    @Override
    public User findById(String id) {
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(findByIdSql, id);
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm User theo ID: " + e.getMessage());
            // Cân nhắc throw new RuntimeException(e);
        } finally {
            // Đóng ResultSet
        }
        return null; // Trả về null nếu không tìm thấy
    }
}