/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.manager; // Hoặc package controller của bạn

import poly.cafe.entity.Drink;
import poly.cafe.ui.CrudController; // Import CrudController

/**
 * Interface điều khiển cho chức năng Quản lý Đồ uống.
 */
public interface DrinkController extends CrudController<Drink> {

    /**
     * Tải và hiển thị danh sách các loại đồ uống (categories)
     * lên các component như JComboBox hoặc JTable (cho việc lọc).
     */
    void fillCategories(); // [cite: 84]

    /**
     * Xử lý hành động chọn file hình ảnh cho đồ uống.
     */
    void chooseFile(); // [cite: 84]
}