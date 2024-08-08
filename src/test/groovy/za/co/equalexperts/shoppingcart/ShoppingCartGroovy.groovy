package za.co.equalexperts.shoppingcart

import org.mockito.MockitoAnnotations
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceDetailRequest
import za.co.equalexperts.shoppingcart.dto.request.ProductPriceRequest
import za.co.equalexperts.shoppingcart.dto.response.ProductDetailResponse
import za.co.equalexperts.shoppingcart.dto.response.ProductPriceResponse

class ShoppingCartGroovy extends Specification {

    RestTemplate restTemplate = new RestTemplate()

    def setup() {
        MockitoAnnotations.initMocks(this)
    }

    def "getCartSummary returns expected summary"() {
        given:
       def url = "http://localhost:8080/shopping-cart/price/cheerios"

        when:
        def response = restTemplate.getForEntity(url, ProductPriceResponse)

        then:
        response != null
    }

    def "getCartSummary returns expected summary of products"() {
        given:
        def url = "http://localhost:8080/shopping-cart/summary"

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def requestBody = new ProductPriceRequest()
        def detailRequest1 = new ProductPriceDetailRequest(name: "cornflakes", quantity: 1)
        def detailRequest2 = new ProductPriceDetailRequest(name: "cornflakes", quantity: 1)
        def detailRequest3 = new ProductPriceDetailRequest(name: "weetabix", quantity: 1)


        def detailRequestsList = [detailRequest1, detailRequest2, detailRequest3]
        requestBody.setProductPriceDetailRequests(detailRequestsList)

        HttpEntity<ProductPriceRequest> requestEntity = new HttpEntity<>(requestBody, headers)

        when:
          def response = restTemplate.exchange(
                  url,
                  HttpMethod.POST,
                  requestEntity,
                  ProductDetailResponse.class
          )

        then:
        response != null
        response.statusCodeValue ==200
        response.getBody().subtotal == 15.02 && response.getBody().total == 16.90
    }


    def "SHOULD FAIL : getCartSummary returns an error for product not available"() {
        given:
        def url = "http://localhost:8080/shopping-cart/summary"

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def requestBody = new ProductPriceRequest()
        def detailRequest1 = new ProductPriceDetailRequest(name: "popcorn", quantity: 1)
        def detailRequest2 = new ProductPriceDetailRequest(name: "cornflakes", quantity: 1)


        def detailRequestsList = [ detailRequest1, detailRequest2]
        requestBody.setProductPriceDetailRequests(detailRequestsList)

        HttpEntity<ProductPriceRequest> requestEntity = new HttpEntity<>(requestBody, headers)

        when:
        def response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ProductDetailResponse.class
        )

        then:
        Exception e = thrown(Exception)
        e.getMessage()
    }
}

