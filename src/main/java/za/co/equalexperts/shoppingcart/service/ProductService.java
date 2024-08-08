package za.co.equalexperts.shoppingcart.service;

import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest;

import java.util.Optional;

public interface ProductService {

    Optional<ProductPriceResponse> getProductByName(String name);

    Optional<ProductDetailResponse> getCartSummary(ProductPriceRequest request);
}
