package com.awesomepizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.*;
import java.util.*;



@SpringBootApplication
public class AwesomePizzaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AwesomePizzaApplication.class, args);
    }
}




@RestController
@RequestMapping("/orders")
class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.addOrder(request.getPizzaType());
    }

    @GetMapping("/{id}")
    public Order getOrderStatus(@PathVariable String id) {
        return orderService.getOrder(id);
    }
}




class Order {
    private final String id;
    private final String pizzaType;
    private String status;

    public Order(String id, String pizzaType) {
        this.id = id;
        this.pizzaType = pizzaType;
        this.status = "In attesa";
    }

    public String getId() { return id; }
    public String getPizzaType() { return pizzaType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}




class OrderRequest {
    private String pizzaType;

    public String getPizzaType() { return pizzaType; }
    public void setPizzaType(String pizzaType) { this.pizzaType = pizzaType; }
}




@Service
class OrderService {
    private final ConcurrentLinkedQueue<Order> orderQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    public Order addOrder(String pizzaType) {
        String id = UUID.randomUUID().toString();
        Order order = new Order(id, pizzaType);
        orderQueue.add(order);
        orderMap.put(id, order);
        return order;
    }

    public Order getOrder(String id) {
        return orderMap.get(id);
    }
}
