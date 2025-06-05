/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.entity.Drink;
import poly.cafe.util.XJdbc;

public class DrinkDAOImpl implements DrinkDAO {

    // Các câu lệnh SQL
    String createSql = "INSERT INTO Drinks(Id, Name, UnitPrice, Discount, Image, Available, CategoryId) VALUES(?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Drinks SET Name=?, UnitPrice=?, Discount=?, Image=?, Available=?, CategoryId=? WHERE Id=?";
    String deleteSql = "DELETE FROM Drinks WHERE Id=?";
    String findAllSql = "SELECT * FROM Drinks";
    String findByIdSql = "SELECT * FROM Drinks WHERE Id=?";
    String findByCategoryIdSql = "SELECT * FROM Drinks WHERE CategoryId=?"; // [cite: 177]

    // Phương thức private để chuyển đổi ResultSet thành đối tượng Drink
    private Drink mapResultSetToDrink(ResultSet rs) throws SQLException {
    return Drink.builder()
            .id(rs.getString("Id"))
            .name(rs.getString("Name"))
            .unitPrice(rs.getDouble("UnitPrice"))
            .discount(rs.getInt("Discount")) // <<<< THAY ĐỔI Ở ĐÂY
            .image(rs.getString("Image"))
            .available(rs.getBoolean("Available"))
            .categoryId(rs.getString("CategoryId"))
            .build();
    }

    @Override
    public Drink create(Drink entity) {
        XJdbc.executeUpdate(createSql,
                entity.getId(),
                entity.getName(),
                entity.getUnitPrice(),
                entity.getDiscount(), // Sẽ là int
                entity.getImage(),
                entity.isAvailable(),
                entity.getCategoryId());
        return entity;
    }

    @Override
    public void update(Drink entity) {
        XJdbc.executeUpdate(updateSql,
                entity.getName(),
                entity.getUnitPrice(),
                entity.getDiscount(),
                entity.getImage(),
                entity.isAvailable(),
                entity.getCategoryId(),
                entity.getId());
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Drink> findAll() {
        List<Drink> drinkList = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(findAllSql);
            while (rs.next()) {
                drinkList.add(mapResultSetToDrink(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn tất cả Drink: " + e.getMessage());
            // throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Cần cơ chế đóng ResultSet
        }
        return drinkList;
    }

    @Override
    public Drink findById(String id) {
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(findByIdSql, id);
            if (rs.next()) {
                return mapResultSetToDrink(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Drink theo ID: " + e.getMessage());
            // throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Cần cơ chế đóng ResultSet
        }
        return null;
    }

    @Override
    public List<Drink> findByCategoryId(String categoryId) {
        List<Drink> drinkList = new ArrayList<>();
        ResultSet rs = null;
        try {
            // Lab2, trang 9 sử dụng XQuery.getBeanList. Chúng ta sẽ dùng XJdbc và map thủ công.
            rs = XJdbc.executeQuery(findByCategoryIdSql, categoryId); //
            while (rs.next()) {
                drinkList.add(mapResultSetToDrink(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Drink theo CategoryId: " + e.getMessage());
            // throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        } finally {
            // Cần cơ chế đóng ResultSet
        }
        return drinkList;
    }
}
