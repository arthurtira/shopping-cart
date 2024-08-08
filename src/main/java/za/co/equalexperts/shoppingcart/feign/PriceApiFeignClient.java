package za.co.equalexperts.shoppingcart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import za.co.equalexperts.shoppingcart.feign.dto.ProductPriceApiResponse;

@Component
@FeignClient(name = "priceApiFeignClient", value = "priceApiFeignClient", url = "https://equalexperts.github.io/")
public interface PriceApiFeignClient {
    @GetMapping("backend-take-home-test-data/{product}.json")
    ResponseEntity<ProductPriceApiResponse> getProductPrice(@PathVariable("product") String product);
}
