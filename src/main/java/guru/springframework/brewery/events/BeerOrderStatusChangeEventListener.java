package guru.springframework.brewery.events;

import guru.springframework.brewery.web.mappers.DateMapper;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import guru.springframework.brewery.web.model.OrderStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BeerOrderStatusChangeEventListener {

    private RestTemplate restTemplate;
    DateMapper dateMapper = new DateMapper();

    public BeerOrderStatusChangeEventListener(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    @EventListener
    public void listen(BeerOrderStatusChangeEvent event){
        System.out.println("I got an order status change event");
        System.out.println(event);

        OrderStatusUpdate update = OrderStatusUpdate.builder()
                .id(event.getBeerOrder().getId())
                .orderId(event.getBeerOrder().getId())
                .version(event.getBeerOrder().getVersion() != null ? event.getBeerOrder().getVersion().intValue() : null)
                .createdDate(dateMapper.asOffsetDateTime(event.getBeerOrder().getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(event.getBeerOrder().getLastModifiedDate()))
                .orderStatus((OrderStatusEnum.valueOf(event.getBeerOrder().getOrderStatus().toString())))
                .customerRef(event.getBeerOrder().getCustomerRef())
                .build();

        try{
            log.debug("Posting to callback url");
            restTemplate.postForObject(event.getBeerOrder().getOrderStatusCallbackUrl(), update, String.class);
        } catch (Throwable t){
            log.error("Error Preforming callback for order: " + event.getBeerOrder().getId(), t);
        }
    }
}