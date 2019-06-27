package com.tedu.sp04.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tedu.sp01.pojo.Item;
import com.tedu.sp01.pojo.Order;
import com.tedu.sp01.pojo.User;
import com.tedu.sp01.service.OrderService;
import com.tedu.sp04.order.feignclient.ItemFeignService;
import com.tedu.sp04.order.feignclient.UserFeignService;
import com.tedu.web.util.JsonResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

	//feign动态生成的接口实现类
	@Autowired
	private ItemFeignService itemFeignService;
	@Autowired
	private UserFeignService userFeignService;
	
	//根据订单id获取订单数据
	//要包含用户信息
	//要包含订单中的上品信息
	@Override
	public Order getOrder(String orderId) {
		// http://localhost:8101/7
		JsonResult<User> user = userFeignService.getUser(7);
		// http://localhost:8001/{orderId}
		JsonResult<List<Item>> items = itemFeignService.getItems(orderId);
		
		Order order = new Order();
		order.setId(orderId);
		order.setUser(user.getData());
		order.setItems(items.getData());
		return order;
	}

	/*
	 * 添加订单时
	 * 要修改商品库存
	 * 修改用户积分
	 */
	@Override
	public void addOrder(Order order) {
		//调用item-service减少商品库存
		itemFeignService.decreaseNumber(order.getItems());
		//调用user-service增加用户积分
		userFeignService.addScore(order.getUser().getId(), 100);
		log.info("保存订单：" + order);
	}

}