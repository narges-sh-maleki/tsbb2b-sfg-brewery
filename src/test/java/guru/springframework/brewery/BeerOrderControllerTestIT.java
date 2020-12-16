package guru.springframework.brewery;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerTestIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
          customer =  customerRepository.findAll().get(0);

    }

    @Test
    void testListOrder(){
        HashMap<String,Object> uriVars = new HashMap<>();
        uriVars.put("customerId",customer.getId());
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/api/v1/customers/{customerId}/orders")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .uriVariables(uriVars);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers); //Update this as per your code

        ResponseEntity<BeerPagedList> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                BeerPagedList.class);

        assertThat(response.getBody().getContent().size()).isEqualTo(1);


    }

}
