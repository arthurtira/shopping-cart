package za.co.equalexperts.shoppingcart.service.impl;

import org.springframework.stereotype.Service;
import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceDetailResponse;
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.feign.PriceApiFeignClient;
import za.co.equalexperts.shoppingcart.feign.dto.ProductPriceApiResponse;
import za.co.equalexperts.shoppingcart.service.ProductService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class ProductServiceImpl implements ProductService {

    private final PriceApiFeignClient priceApiFeignClient;

    public ProductServiceImpl(PriceApiFeignClient priceApiFeignClient) {
        this.priceApiFeignClient = priceApiFeignClient;
    }

    @Override
    public ProductPriceResponse getProductByName(String name) {
        ProductPriceApiResponse productPriceApiResponse= priceApiFeignClient.getProductPrice(name).getBody();
        ProductPriceResponse response = ProductPriceResponse.builder().price(productPriceApiResponse.getPrice()).title(productPriceApiResponse.getTitle()).status("Active").build();
        return response;
    }

    @Override
    public ProductDetailResponse getCartSummary(ProductPriceRequest request) {
        Map<String, ProductPriceDetailResponse> products = new HashMap<>();
        request.getProductPriceDetailRequests().forEach(productPriceDetailRequest -> {
            ProductPriceApiResponse response = priceApiFeignClient.getProductPrice(productPriceDetailRequest.getName()).getBody();
            if (Objects.nonNull(response)) {

                String name = response.getTitle();
                BigDecimal price = response.getPrice();
                int quantity = productPriceDetailRequest.getQuantity();

                if (products.containsKey(name)) {
                    ProductPriceDetailResponse existing = products.get(name);
                    existing.setQuantity(existing.getQuantity() + quantity);
                } else {

                    ProductPriceDetailResponse priceDetailResponse = ProductPriceDetailResponse.builder()
                            .unitPrice(price)
                            .quantity(quantity)
                            .name(name)
                            .build();
                    products.put(name, priceDetailResponse);

                }
            }

        });
        if (!products.isEmpty()){

            BigDecimal subtotal = BigDecimal.ZERO;
            for (ProductPriceDetailResponse detail : products.values()) {
                subtotal = subtotal.add(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            }

            BigDecimal tax = subtotal.multiply(new BigDecimal("0.125"));
            BigDecimal total = subtotal.add(tax);

            subtotal = subtotal.setScale(2, ROUND_HALF_UP);
            tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
            total = total.setScale(2, ROUND_HALF_UP);

            return ProductDetailResponse.builder()
                    .priceDetailResponses(products.values().stream().toList())
                    .subtotal(subtotal).
                    total(total).
                    tax(tax).
                    build();

        }
        return null;
    }
}
