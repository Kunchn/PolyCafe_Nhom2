/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui; // Hoặc package controller của bạn

public interface CrudController<Entity> {
    void open(); // Xử lý mở cửa sổ [cite: 213]
    void setForm(Entity entity); // Hiển thị thực thể lên form [cite: 214]
    Entity getForm(); // Tạo thực thể từ dữ liệu form [cite: 214]
    void fillToTable(); // Tải dữ liệu và đổ lên bảng [cite: 215]
    void edit(); // Hiển thị dữ liệu của hàng được chọn lên form [cite: 215]
    void create(); // Tạo thực thể mới [cite: 216]
    void update(); // Cập nhật thực thể đang xem [cite: 216]
    void delete(); // Xóa thực thể đang xem [cite: 217]
    void clear(); // Xóa trắng form [cite: 217]
    void setEditable(boolean editable); // Thay đổi trạng thái form [cite: 217]
    void checkAll(); // Tích chọn tất cả các hàng trên bảng [cite: 218]
    void uncheckAll(); // Bỏ tích chọn tất cả các hàng trên bảng [cite: 219]
    void deleteCheckedItems(); // Xóa các thực thể được tích chọn [cite: 219]
    void moveFirst(); // Hiển thị thực thể đầu tiên [cite: 220]
    void movePrevious(); // Hiển thị thực thể kế trước [cite: 220]
    void moveNext(); // Hiển thị thực thể kế sau [cite: 221]
    void moveLast(); // Hiển thị thực thể cuối cùng [cite: 222]
    void moveTo(int rowIndex); // Hiển thị thực thể tại vị trí [cite: 223]
}