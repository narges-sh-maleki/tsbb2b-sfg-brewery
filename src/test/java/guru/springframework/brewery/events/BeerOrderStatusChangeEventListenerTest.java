package guru.springframework.brewery.events;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import guru.springframework.brewery.domain.BeerOrder;
import guru.springframework.brewery.domain.OrderStatusEnum;
import guru.springframework.brewery.web.model.OrderStatusUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WireMockExtension.class)
//@SpringBootTest
class BeerOrderStatusChangeEventListenerTest {

    BeerOrderStatusChangeEventListener beerOrderStatusChangeEventListener;

    @Managed
    WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());


    @BeforeEach
    void setUp() {
        beerOrderStatusChangeEventListener = new BeerOrderStatusChangeEventListener(new RestTemplateBuilder());
    }

    @Test
    void listen() {
        //given
        BeerOrder beerOrder = BeerOrder.builder()
                .id(UUID.randomUUID())
                .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                .lastModifiedDate(Timestamp.valueOf(LocalDateTime.now()))
                .orderStatus(OrderStatusEnum.READY)
                .customerRef("yuej")
                .orderStatusCallbackUrl("http://localhost:" + wireMockServer.port() + "/update")
                .build();
        BeerOrderStatusChangeEvent beerOrderStatusChangeEvent = new BeerOrderStatusChangeEvent(beerOrder, OrderStatusEnum.NEW);
        wireMockServer.stubFor(post("/update").willReturn(ok()));

        //when
        beerOrderStatusChangeEventListener.listen(beerOrderStatusChangeEvent);

        //then
        verify(1, postRequestedFor(urlEqualTo("/update")));

    }
}