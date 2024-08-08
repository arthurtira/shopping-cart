package za.co.equalexperts.shoppingcart.service.impl;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceDetailResponse;
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.feign.PriceApiFeignClient;
import za.co.equalexperts.shoppingcart.service.ProductService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductService {
    private final static BigDecimal TAX_RATE = new BigDecimal("0.125");
    private final PriceApiFeignClient priceApiFeignClient;

    public ProductServiceImpl(PriceApiFeignClient priceApiFeignClient) {
        this.priceApiFeignClient = priceApiFeignClient;
    }

    @Override
    public Optional<ProductPriceResponse> getProductByName(String name) {
        var response= priceApiFeignClient.getProductPrice(name);
        if (response.getStatusCode().is2xxSuccessful()) {
            final var productPriceApiResponse = response.getBody();
            return Optional.of(ProductPriceResponse.builder()
                    .price(productPriceApiResponse.getPrice())
                    .title(productPriceApiResponse.getTitle())
                    .status("Active")
                    .build());
        } else if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
            return Optional.empty();
        }
        throw new RuntimeException("Error getting product pricing - status code: " + response.getStatusCode().value());
    }

    @Override
    public Optional<ProductDetailResponse> getCartSummary(ProductPriceRequest request) {
        final var  products = new HashMap<String, ProductPriceDetailResponse>();
        request.getProductPriceDetailRequests().forEach(productPriceDetailRequest -> {
            var response = priceApiFeignClient.getProductPrice(productPriceDetailRequest.getName()).getBody();
            if (Objects.nonNull(response)) {

                var name = response.getTitle();
                var price = response.getPrice();
                var quantity = productPriceDetailRequest.getQuantity();

                if (products.containsKey(name)) {
                    ProductPriceDetailResponse existing = products.get(name);
                    existing.setQuantity(existing.getQuantity() + quantity);
                } else {
                    var priceDetailResponse = ProductPriceDetailResponse.builder()
                            .unitPrice(price)
                            .quantity(quantity)
                            .name(name)
                            .build();
                    products.put(name, priceDetailResponse);
                }
            }
        });

        if (!products.isEmpty()){
            var subtotal = BigDecimal.valueOf(products.values().stream()
                    .map(r -> r.getUnitPrice().multiply(new BigDecimal(r.getQuantity())))
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum());

            var tax = subtotal.multiply(TAX_RATE);
            var total = subtotal.add(tax);

            return Optional.of(ProductDetailResponse.builder()
                    .priceDetailResponses(products.values().stream().toList())
                    .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                    .total(total.setScale(2, RoundingMode.HALF_UP))
                    .tax(tax.setScale(2, RoundingMode.HALF_UP))
                    .build()
            );
        }
        return Optional.empty();
    }
}
