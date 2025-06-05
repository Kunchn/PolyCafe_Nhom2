/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import poly.cafe.entity.Category; // Import lớp Category

/**
 * Interface truy xuất dữ liệu cho thực thể Category.
 * Kế thừa các phương thức CRUD cơ bản từ CrudDAO.
 */
public interface CategoryDAO extends CrudDAO<Category, String> {
    // Theo Lab2, trang 6, CategoryDAO không thêm phương thức nào mới ngoài CrudDAO.
    // Nếu sau này cần các phương thức đặc thù cho Category, chúng sẽ được thêm ở đây.
}