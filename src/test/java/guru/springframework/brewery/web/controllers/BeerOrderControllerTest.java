package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BeerOrderControllerTest {

    @Autowired
    BeerOrderController beerOrderController;

    @MockBean
    BeerOrderService beerOrderService;

    //@Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerOrderController).build();

    }

    @Test
    void listOrders() throws Exception {
        //given
        BeerOrderDto beerOrderDto1 = BeerOrderDto.builder()
                                        .customerRef("testOrder1")
                                        .customerId(UUID.randomUUID())
                                        .build();
        List<BeerOrderDto> beerOrderDtoList = new ArrayList<>();
        beerOrderDtoList.add(beerOrderDto1);
        BeerOrderPagedList beerOrderPagedList = new BeerOrderPagedList(beerOrderDtoList);
        when(beerOrderService.listOrders(any(UUID.class), any(Pageable.class))).thenReturn(beerOrderPagedList);

        //when and then
        mockMvc.perform(get("/api/v1/customers/{customerId}/orders", beerOrderDto1.getCustomerId())
                            .param("pageNumber", "0")
                            .param("pageSize", "10"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content[0].customerRef",is("testOrder1")))
                        .andExpect(jsonPath("$.content", hasSize(1)))
                        .andDo(print());
    }

    @Test
    void getOrder() throws Exception {
        //given
        BeerOrderDto beerOrderDto1 = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .customerRef("testOrder1")
                .customerId(UUID.randomUUID())
                .build();

        when(beerOrderService.getOrderById(beerOrderDto1.getCustomerId(),beerOrderDto1.getId())).thenReturn(beerOrderDto1);

        //when and then
        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}",beerOrderDto1.getCustomerId(),beerOrderDto1.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.customerRef",is("testOrder1")))
                    //.andExpect(jsonPath("$.content",hasSize(1)))
        ;
    }
}