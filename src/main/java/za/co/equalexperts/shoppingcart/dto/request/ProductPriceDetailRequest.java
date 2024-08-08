package za.co.equalexperts.shoppingcart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceDetailRequest {
    private String name;
    private int quantity;
}
