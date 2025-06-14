/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author hungpc
 */
package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetail {
    private Long id; // BIGINT
    private Long billId; // BIGINT
    private String drinkId; // NVARCHAR(20)
    private double unitPrice; // FLOAT
    private int discount; // INT (lưu 10 cho 10%)
    private int quantity; // INT
}