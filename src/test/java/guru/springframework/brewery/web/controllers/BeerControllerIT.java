package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getBeerList() {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/api/v1/beer")
                .queryParam("pageNumber", 0)
                .queryParam("pageSize", 10)
                .queryParam("beerName", "Galaxy Cat")
                .queryParam("beerStyle", "PALE_ALE");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers); //Update this as per your code

        ResponseEntity<BeerPagedList> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                BeerPagedList.class);


        // BeerPagedList beerPagedList = restTemplate.getForObject("/api/v1/beer",BeerPagedList.class);

    }
}
