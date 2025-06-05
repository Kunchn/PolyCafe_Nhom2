/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import poly.cafe.entity.User; // Import lớp User đã tạo

/**
 * Interface truy xuất dữ liệu cho thực thể User.
 * Kế thừa các phương thức CRUD cơ bản từ CrudDAO.
 */
public interface UserDAO extends CrudDAO<User, String> {
    // Hiện tại, theo Lab 2, chúng ta chưa cần thêm phương thức nào đặc thù ở đây.
    // Nếu sau này cần các phương thức như findByRole(), findByFullname(), ...
    // chúng ta sẽ thêm chúng vào đây.
}