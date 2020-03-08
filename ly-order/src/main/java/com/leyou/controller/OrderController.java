package com.leyou.controller;

import com.leyou.dto.OrderDto;
import com.leyou.enums.PayState;
import com.leyou.pojo.Order;
import com.leyou.pojo.ReceiverAddress;
import com.leyou.service.OrderService;
import com.leyou.utils.PayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.controller
 * @version: 1.0
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayHelper payHelper;

    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    @PostMapping("order")
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto){

        Long orderId = orderService.createOrder(orderDto);
        return ResponseEntity.ok(orderId);
    }

    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    @GetMapping("order/{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id){

        Order order = orderService.queryOrderById(id);
        return ResponseEntity.ok(order);
    }


    /**
     * 根据登录用户的id查询收货人地址
     * @param id
     * @return
     */
    @GetMapping("receiver")
    public  ResponseEntity<List<ReceiverAddress>> queryReceiverAddressById(@RequestParam("id") Long id){
        List<ReceiverAddress> receiverAddresses=  orderService.queryReceiverAddressById(id);
        return ResponseEntity.ok(receiverAddresses);
    }

    /**
     * 根据订单ID生成付款链接
     * @param orderId
     * @return
     */
    @GetMapping("order/url/{id}")
    public  ResponseEntity<String> createPayUrl(@PathVariable("id") Long orderId){
        String payUrl= orderService.createPayUrl(orderId);
        return  ResponseEntity.ok(payUrl);
    }

    /**
     *  查询订单状态，用于前端页面跳转，如跳转到支付成功页面或支付失败页面
     * @param orderId
     */
    @GetMapping("order/state/{id}")
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long orderId){
        Integer payState = orderService.queryPayState(orderId);
        return ResponseEntity.ok(payState);
    }
}
