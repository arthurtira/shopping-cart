package za.co.equalexperts.shoppingcart.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest;
import za.co.equalexperts.shoppingcart.service.ProductService;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    private final ProductService productService;

    public ShoppingCartController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value="/price/{productName}",produces = "application/json")
    public ResponseEntity<ProductPriceResponse> getProductByName(@PathVariable String productName){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductByName(productName));
    }

    @PostMapping(value="/summary",produces = "application/json")
    public ResponseEntity<ProductDetailResponse> getCartDetails(@RequestBody ProductPriceRequest productPriceRequest){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getCartSummary(productPriceRequest));
    }
}
