package za.co.equalexperts.shoppingcart.service;

import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest;

public interface ProductService {

    ProductPriceResponse getProductByName(String name);

    ProductDetailResponse getCartSummary(ProductPriceRequest request);
}
