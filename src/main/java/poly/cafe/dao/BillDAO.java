/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.Bill;

public interface BillDAO extends CrudDAO<Bill, Long> {
    // Tìm các hóa đơn theo trạng thái (ví dụ: 0 là đang chờ)
    List<Bill> findByStatus(int status);
}
