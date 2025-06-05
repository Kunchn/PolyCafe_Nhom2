/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;

/**
 * Interface chung cho các thao tác CRUD (Create, Read, Update, Delete)
 *
 * @param <T>  Kiểu thực thể (Entity)
 * @param <ID> Kiểu dữ liệu của khóa chính (Primary Key)
 */
public interface CrudDAO<T, ID> {

    /**
     * Thêm một thực thể mới vào CSDL.
     * @param entity Thực thể cần thêm.
     * @return Thực thể đã thêm (có thể đã được cập nhật, ví dụ: ID tự tăng).
     */
    T create(T entity); 

    /**
     * Cập nhật thông tin một thực thể trong CSDL.
     * @param entity Thực thể với thông tin đã cập nhật.
     */
    void update(T entity); 

    /**
     * Xóa một thực thể khỏi CSDL dựa trên khóa chính.
     * @param id Khóa chính của thực thể cần xóa.
     */
    void deleteById(ID id);

    /**
     * Lấy tất cả các thực thể từ CSDL.
     * @return Danh sách tất cả các thực thể.
     */
    List<T> findAll(); 

    /**
     * Tìm một thực thể dựa trên khóa chính.
     * @param id Khóa chính của thực thể cần tìm.
     * @return Thực thể tìm thấy hoặc null nếu không tồn tại.
     */
    T findById(ID id);
}