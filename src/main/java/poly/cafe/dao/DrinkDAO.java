/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.Drink; // Import lớp Drink

/**
 * Interface truy xuất dữ liệu cho thực thể Drink.
 * Kế thừa các phương thức CRUD cơ bản từ CrudDAO và thêm các phương thức đặc thù.
 */
public interface DrinkDAO extends CrudDAO<Drink, String> {

    /**
     * Tìm tất cả đồ uống thuộc một loại cụ thể.
     * @param categoryId Mã của loại đồ uống.
     * @return Danh sách các đồ uống thuộc loại đó.
     */
    List<Drink> findByCategoryId(String categoryId); // [cite: 167]
}