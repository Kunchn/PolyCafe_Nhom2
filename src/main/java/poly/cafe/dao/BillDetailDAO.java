/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.BillDetail;

public interface BillDetailDAO extends CrudDAO<BillDetail, Long> {
    // Tìm tất cả chi tiết của một hóa đơn cụ thể
    List<BillDetail> findByBillId(Long billId);
}