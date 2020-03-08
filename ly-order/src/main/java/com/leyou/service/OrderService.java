package com.leyou.service;

import com.leyou.client.CartClient;
import com.leyou.client.GoodsClient;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.IdWorker;
import com.leyou.dto.OrderDto;
import com.leyou.entity.UserInfo;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayState;
import com.leyou.interceptor.UserInterceptor;
import com.leyou.item.pojo.Sku;
import com.leyou.mapper.OrderDetailMapper;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.mapper.ReceiverAddressMapper;
import com.leyou.pojo.Order;
import com.leyou.pojo.OrderDetail;
import com.leyou.pojo.OrderStatus;
import com.leyou.pojo.ReceiverAddress;
import com.leyou.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/22
 * @Description: com.leyou.service
 * @version: 1.0
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ReceiverAddressMapper receiverAddressMapper;

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CartClient cartClient;

    @Autowired
    private PayHelper payHelper;

    /**
     * 提交订单
     * @param orderDto
     * @return
     */
    @Transactional
    public Long createOrder(OrderDto orderDto) {

        // 1.新增订单
        Order order = new Order();
        // 1.1订单编号雪花算法生成 、订单基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);  //订单id
        order.setCreateTime(new Date());//订单创建时间
        order.setPaymentType(orderDto.getPaymentType());//订单支付类型

        // 1.2用户（下单者）信息 通过拦截器
        UserInfo userInfo = UserInterceptor.getLoginUser();
        order.setUserId(userInfo.getId());//订单所属用户id
        order.setBuyerNick(userInfo.getUsername());//订单所属用户的名称
        order.setBuyerRate(false);//订单是否评价，创建订单时默认是未评价,确认收货后才会进行评价

        // 1.3收货人地址  注意收货人和下单者未必是同一个人
        ReceiverAddress receiver = receiverAddressMapper.selectByPrimaryKey(orderDto.getAddressId());
        order.setReceiver(receiver.getName());// 收货人全名
        order.setReceiverMobile(receiver.getPhone());// 移动电话
        order.setReceiverZip(receiver.getZipCode());// 邮政编码,如：310001
        order.setReceiverState(receiver.getState()); // 省份
        order.setReceiverCity(receiver.getCity());// 城市
        order.setReceiverDistrict(receiver.getDistrict());// 区/县
        order.setReceiverAddress(receiver.getAddress());// 收货地址，如：xx路xx号

        // 1.4订单金额
        //获取订单中的商品（只包括skuid和num数量）
        List<CartDto> cartsDto = orderDto.getCarts();
        //转为map方便后面的计算 其中key是skuid  value是num购买数量
        Map<Long, Integer> map = cartsDto.stream()
                               .collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        //获取cartsDto中所有skuIds集合
        Set<Long> longs = map.keySet();
        //将set集合转为list集合
        List<Long> skuIds = new ArrayList<>(longs);
        List<Sku> skus = goodsClient.querySkusBySkuIds(skuIds);
        long totalPay =0L;
        for (Sku sku : skus) {
            Long skuid = sku.getId();//获取skuid
            Long price = sku.getPrice();//获取商品价格
            Integer num = map.get(skuid);
            totalPay += price * num;  //累加计算
        }
        order.setTotalPay(totalPay);//支付总金额
        order.setActualPay(totalPay + order.getPostFee() - 0);//实付金额 = 支付总金额 + 邮费 - 优惠金额
        int count = orderMapper.insertSelective(order);
        if (count!=1){
            throw new LyExcception(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        // 2新增订单详情
        List<OrderDetail> orderDetails =new ArrayList<>();
        for (Sku sku : skus) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            //获取以,分隔字符串的第一个
            String image = StringUtils.substringBefore(sku.getImages(), ",");
            orderDetail.setImage(image);
            orderDetail.setNum(map.get(sku.getId()));
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetails.add(orderDetail);
        }
         count = orderDetailMapper.insertList(orderDetails);
        if (count != orderDetails.size()){
            throw new LyExcception(ExceptionEnum.CREATE_ORDER_DETAIL_ERROR);
        }

        // 3新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1){
            throw new LyExcception(ExceptionEnum.CREATE_ORDER_STATUS_ERROR);
        }
        // 4减少库存
        goodsClient.decreaseStock(cartsDto);


        return  orderId;
    }
    /**
     * 根据登录用户的id查询收货人地址
     * @param id
     * @return
     */
    public List<ReceiverAddress> queryReceiverAddressById(Long id) {
        ReceiverAddress receiverAddress = new ReceiverAddress();
        receiverAddress.setLoginId(id);
        List<ReceiverAddress> addressList = receiverAddressMapper.select(receiverAddress);
        if(CollectionUtils.isEmpty(addressList)){
            throw new LyExcception(ExceptionEnum.RECEIVER_NOT_FOUND);
        }
        return addressList;
    }
    /**
     * 根据订单id查询订单
     * @param id
     * @return
     */
    public Order queryOrderById(Long id) {
        //查询订单表
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null){
            throw new LyExcception(ExceptionEnum.ORDER_NOT_FOUND);
        }

        //查询订单详情表
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetails)){
            throw new LyExcception(ExceptionEnum.ORDER_DETAIL_FOUND);
        }
        order.setOrderDetails(orderDetails);

        //查询订单状态表
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus == null){
            throw new LyExcception(ExceptionEnum.ORDER_STATUS_FOUND);
        }
        order.setOrderStatus(orderStatus);

        return order;
    }
    /**
     * 根据订单ID生成付款链接
     * @param orderId
     * @return
     */
    public String createPayUrl(Long orderId) {
        //查询订单
        Order order = this.queryOrderById(orderId);
        if (order == null){
            throw new LyExcception(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //健壮性判断：所查询的订单状态如果是已支付，则就无需在获取支付连接
        Integer status = order.getOrderStatus().getStatus();
        if(OrderStatusEnum.UN_PAY.value()!= status){ //注意不要用equals比价数字
            throw new LyExcception(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        //获取订单的实付金额
        Long actualPay = order.getActualPay();//开发时使用
        actualPay =1L;//测试花费一分钱
        
        //商品描述
        OrderDetail orderDetail = order.getOrderDetails().get(0);
        String skuDesc = orderDetail.getTitle();

        String payUrl = payHelper.createPayUrl(orderId, actualPay, skuDesc);
        if(StringUtils.isBlank(payUrl)){
            throw new LyExcception(ExceptionEnum.GET_PAY_URL_ERROR);
        }
        return payUrl;
    }

    /**
     * 处理微信的回调
     * @param result
     */
    public void handlerNotify(Map<String, String> result) {
        //判断通信标识
        payHelper.isSuccess(result);

        //签名校验
        payHelper.isValidSign(result);

        //校验微信传递过来的金额和我们订单的实付金额是否一致
        String totalFeeStr = result.get("total_fee");//获取微信传递过来的金额
        String tradeNo = result.get("out_trade_no");//获取微信传递过来的订单编号
        if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)){
            throw new LyExcception(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        //获取微信传递过来的金额
        Long totalFee = Long.valueOf(totalFeeStr);
        //查询我们自己系统中的订单，为获取金额做准备
        Order order = orderMapper.selectByPrimaryKey(tradeNo);
        //获取我们系统的订单实付金额
        Long actualPay = order.getActualPay();
//        if(totalFee != actualPay){//微信传递过来的金额和我们订单系统中的实付金额不一致报错！开发时使用
//            //金额不相等
//            throw new LyExcception(ExceptionEnum.WXPAY_NOT_EQUAL_ORDERPAY);
//        }
        //测试时使用
        if(totalFee != 1){
            //金额不相等
            throw new LyExcception(ExceptionEnum.WXPAY_NOT_EQUAL_ORDERPAY);
        }


        //以上数据校验全部合法，接下来修改我们的订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(Long.valueOf(tradeNo));
        //付款时间
        orderStatus.setPaymentTime(new Date());
        //订单状态修改为：已付款
        orderStatus.setStatus(OrderStatusEnum.PAYED.value());
        int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if(count!=1){
            //更新订单状态失败
            throw new LyExcception(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }

        log.info("[微信回调] 订单支付成功，订单编号：{}",tradeNo);
    }

    /**
     *  查询订单状态，用于前端页面跳转，如跳转到支付成功页面或支付失败页面
     * @param orderId
     */
    public Integer queryPayState(Long orderId) {
        //从订单状态表查询订单是否支付
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null){
            throw new LyExcception(ExceptionEnum.ORDER_STATUS_FOUND);
        }
        //获取订单状态
        Integer status = orderStatus.getStatus();
        if (status!=OrderStatusEnum.UN_PAY.value()){
            //如果是已支付，那么是真的已经支付了，因为在调用微信回调时，已经做了订单状态的更改
            return PayState.SUCCESS.getValue();
        }

        //如果是未支付，其实并不一定是没有支付，可能是微信还没有进行回调，这时我们需要 从微信那里查询订单状态
        Integer payState = payHelper.queryPayState(orderId);
        return payState;
    }
}
