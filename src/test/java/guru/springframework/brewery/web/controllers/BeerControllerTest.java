package guru.springframework.brewery.web.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
//@WebMvcTest(BeerController.class)
@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    @Mock //Mockito
    //@MockBean
    BeerService beerService;

    @InjectMocks //Mockito
    //@Autowired
    BeerController beerController;

    //@Autowired @WebMvcTest
    MockMvc mockMvc;

    static BeerDto beerDto;

   // @Autowired
   // MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

    @BeforeAll
    static void beforeAll() {
        beerDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .beerName("Heinken")
                .createdDate(OffsetDateTime.now())
                .build();
    }

    @BeforeEach
    void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(beerController)
              //  .setMessageConverters(jackson2HttpMessageConverter)
                        .build();
    }
/*
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
*/
    @Test
    void listBeers() throws Exception {
        //given
        List<BeerDto> beerDtoList = new ArrayList<>();
        beerDtoList.add(beerDto);
        BeerPagedList beerPagedList = new BeerPagedList(beerDtoList);
        given(beerService.listBeers(anyString(), any(BeerStyleEnum.class), any(PageRequest.class))).willReturn(beerPagedList);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        //when and then
        mockMvc.perform(get("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .param("beerName", "heinken")
                .param("beerStyle", "ALE"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(beerDto.getId().toString())))
                .andExpect(jsonPath("$.content[0].createdDate", is(fmt.format(beerDto.getCreatedDate()))))
                .andExpect(status().isOk());
    }

    @Test
    void getBeerById() throws Exception {
        //given

        when(beerService.findBeerById(any())).thenReturn(beerDto);

        //when and then
        mockMvc.perform(get("/api/v1/beer/" + beerDto.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(beerDto.getId().toString())));
    }



}