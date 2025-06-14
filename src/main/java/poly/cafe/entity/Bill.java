/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private Long id; // BIGINT
    private String username; // NVARCHAR(20)
    private Integer cardId; // INT - Có thể không dùng trong giao diện mới, nhưng cứ tạo theo CSDL

    @Builder.Default
    private Date checkin = new Date(); // DATETIME

    private Date checkout; // DATETIME
    private int status; // INT (ví dụ: 0=Chờ, 1=Hoàn thành, 2=Hủy)
}