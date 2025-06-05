package poly.cafe.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Drink {
    private String id;
    private String name;
    private double unitPrice; // Giữ nguyên unitPrice là double
    private int discount;     // <<<< THAY ĐỔI Ở ĐÂY

    @Builder.Default
    private String image = "product.png";

    private boolean available;
    private String categoryId;
}