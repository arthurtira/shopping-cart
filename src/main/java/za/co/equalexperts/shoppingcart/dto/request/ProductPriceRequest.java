package za.co.equalexperts.shoppingcart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceRequest {
    private List<ProductPriceDetailRequest> productPriceDetailRequests;
}
