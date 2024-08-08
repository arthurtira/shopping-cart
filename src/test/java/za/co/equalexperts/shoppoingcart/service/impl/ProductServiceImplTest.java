package za.co.equalexperts.shoppoingcart.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse;
import za.co.equalexperts.shoppingcart.feign.PriceApiFeignClient;
import za.co.equalexperts.shoppingcart.feign.dto.ProductPriceApiResponse;
import za.co.equalexperts.shoppingcart.service.impl.ProductServiceImpl;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private PriceApiFeignClient priceApiFeignClient;

    @Test
    void must_call_price_api_invoked(){
        ProductPriceApiResponse productPriceApiResponse = ProductPriceApiResponse.builder().price(BigDecimal.valueOf(2.45)).title("cheerios").build();
        ResponseEntity<ProductPriceApiResponse> mockProductprice= ResponseEntity.status(HttpStatus.OK).body(productPriceApiResponse);
        Mockito.when(priceApiFeignClient.getProductPrice(Mockito.anyString())).thenReturn(mockProductprice);
        ProductPriceResponse cheerios = productService.getProductByName("cheerios");
        Mockito.verify(priceApiFeignClient, Mockito.atMostOnce()).getProductPrice(any());
    }
}
