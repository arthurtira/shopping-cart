package za.co.equalexperts.shoppingcart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private List<ProductPriceDetailResponse> priceDetailResponses;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
}
