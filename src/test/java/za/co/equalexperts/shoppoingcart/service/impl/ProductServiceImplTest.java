package za.co.equalexperts.shoppoingcart.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.equalexperts.shoppingcart.feign.PriceApiFeignClient;
import za.co.equalexperts.shoppingcart.feign.dto.ProductPriceApiResponse;
import za.co.equalexperts.shoppingcart.service.impl.ProductServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private PriceApiFeignClient priceApiFeignClient;

    @Test
    @DisplayName("Should invoke pricing api when get price method is invoked")
    void getProduct_invokesPricingAPI(){
        var productPriceApiResponse = ProductPriceApiResponse.builder()
                .price(BigDecimal.valueOf(2.45))
                .title("cheerios")
                .build();

        var mockProductprice= ResponseEntity.status(HttpStatus.OK).body(productPriceApiResponse);
        Mockito.when(priceApiFeignClient.getProductPrice("cheerios")).thenReturn(mockProductprice);

        final var product = productService.getProductByName("cheerios");

        assertTrue(product.isPresent());
        assertEquals("cheerios", product.get().getTitle());
        assertEquals(new BigDecimal("2.45"), product.get().getPrice());
        Mockito.verify(priceApiFeignClient, Mockito.atMostOnce()).getProductPrice("cheerios");
    }

    @Test
    @DisplayName("Should return empty if product is not found")
    void getProduct_productDoesNotExist(){
        Mockito.when(priceApiFeignClient.getProductPrice("cheerios")).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        final var product = productService.getProductByName("cheerios");

        assertTrue(product.isEmpty());
        Mockito.verify(priceApiFeignClient, Mockito.atMostOnce()).getProductPrice("cheerios");
    }

    @Test
    @DisplayName("Should throw exception if pricing api returns error")
    void getProduct_throwsException(){
        Mockito.when(priceApiFeignClient.getProductPrice("cheerios"))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());

        final var exception = assertThrows(RuntimeException.class, () ->productService.getProductByName("cheerios"));

        assertNotNull(exception);
        assertEquals("Error getting product pricing - status code: 502", exception.getMessage());
        Mockito.verify(priceApiFeignClient, Mockito.atMostOnce()).getProductPrice("cheerios");
    }
}
