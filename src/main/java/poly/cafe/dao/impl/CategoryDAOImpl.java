/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.entity.Category;
import poly.cafe.util.XJdbc;

public class CategoryDAOImpl implements CategoryDAO {

    // Các câu lệnh SQL dựa trên Lab2, trang 7
    String createSql = "INSERT INTO Categories(Id, Name) VALUES(?, ?)"; // [cite: 170]
    String updateSql = "UPDATE Categories SET Name=? WHERE Id=?"; // [cite: 171]
    String deleteSql = "DELETE FROM Categories WHERE Id=?"; // [cite: 171]
    String findAllSql = "SELECT * FROM Categories"; // [cite: 172]
    String findByIdSql = "SELECT * FROM Categories WHERE Id=?"; // [cite: 172]

    // Phương thức private để chuyển đổi ResultSet thành đối tượng Category
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        return Category.builder()
                .id(rs.getString("Id"))
                .name(rs.getString("Name"))
                .build();
    }

    @Override
    public Category create(Category entity) {
        // Thực thi theo Lab2, trang 7 [cite: 171]
        XJdbc.executeUpdate(createSql,
                entity.getId(),
                entity.getName());
        return entity;
    }

    @Override
    public void update(Category entity) {
        // Thực thi theo Lab2, trang 7 [cite: 171]
        XJdbc.executeUpdate(updateSql,
                entity.getName(),
                entity.getId());
    }

    @Override
    public void deleteById(String id) {
        // Thực thi theo Lab2, trang 7 [cite: 171]
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Category> findAll() {
        List<Category> categoryList = new ArrayList<>();
        ResultSet rs = null;
        try {
            // Lab2, trang 7 sử dụng XQuery.getEntityList. Chúng ta sẽ dùng XJdbc và map thủ công. [cite: 172]
            rs = XJdbc.executeQuery(findAllSql);
            while (rs.next()) {
                categoryList.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn tất cả Category: " + e.getMessage());
            // throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Cần cơ chế đóng ResultSet trong XJdbc hoặc ở đây
        }
        return categoryList;
    }

    @Override
    public Category findById(String id) {
        ResultSet rs = null;
        try {
            // Lab2, trang 7 sử dụng XQuery.getSingleBean. Chúng ta sẽ dùng XJdbc và map thủ công. [cite: 172]
            rs = XJdbc.executeQuery(findByIdSql, id);
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Category theo ID: " + e.getMessage());
            // throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Cần cơ chế đóng ResultSet trong XJdbc hoặc ở đây
        }
        return null;
    }
}