package service;

import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.Article;
import domain.Order;
import domain.OrderDTO;
import domain.OrderStatus;
import domain.Restaurant;
import domain.User;
import domain.UserType;

@Path("/api")
public class OrderService {

	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	@Context
	Response response;

	RestaurantService rs = new RestaurantService();
	ApplicationService as = new ApplicationService();
	

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public String generateString(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}

		return builder.toString();
	}

	@POST
	@Path("/orders")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<OrderDTO> placeOrder(OrderDTO orderDTO, @Context HttpServletRequest request) {
		ServiceResponse<OrderDTO> retVal = new ServiceResponse<OrderDTO>();
		if (orderDTO.getArticles().isEmpty()) {
			retVal.success = false;
			retVal.message = "Morate izabrati bar jedan artikal.";
			return retVal;
		}
		ServiceResponse<Restaurant> response = rs.getRestaurant(orderDTO.getRestaurant());
		Restaurant restaurant = response.data;
		Order order = new Order();
		HttpSession session = request.getSession(true);
		order.setBuyer((User) session.getAttribute("loggedUser"));
		order.setExtraInfo(orderDTO.getNotes());
		order.setStatus(OrderStatus.ordered);
		order.setDate(new Date());
		order.setDeliverer(null);

		for (Article a : restaurant.getArticles()) {
			if (!a.isDeleted()) {
				for (int i = 0; i < orderDTO.getArticles().size(); i++) {
					if (a.getName().equals(orderDTO.getArticles().get(i))) {
						order.getItems().add(a);
					}
				}

			}
		}
		order.setId(generateString(10));
		InitData.getInstance().getDataStore().getOrders().add(order);
		InitData.getInstance().serialize();
		retVal.success = true;
		return retVal;
	}

	@GET
	@Path("/orders")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Order>> getOrders() {
		ServiceResponse<ArrayList<Order>> retVal = new ServiceResponse<ArrayList<Order>>();
		ArrayList<Order> orders = new ArrayList<Order>();

		for (Order o : InitData.getInstance().getDataStore().getOrders()) {
			if (!o.isDeleted()) {
				orders.add(o);
			}
		}

		retVal.data = orders;

		if (orders.isEmpty()) {
			retVal.success = false;
			retVal.message = "Nema narudzbina.";
			return retVal;
		}

		retVal.success = true;
		return retVal;
	}

	@GET
	@Path("/orders/devArea")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Order>> getDeliveryOrders() {
		ServiceResponse<ArrayList<Order>> retVal = new ServiceResponse<ArrayList<Order>>();
		ArrayList<Order> orderList = new ArrayList<Order>();

		for (Order o : InitData.getInstance().getDataStore().getOrders()) {
			if (!o.isDeleted()) {
				if (o.getStatus().equals(OrderStatus.ordered)) {
					orderList.add(o);
				}
			}

		}
		retVal.data = orderList;

		if (retVal.data.isEmpty()) {
			retVal.success = false;
			retVal.message = "Nema porudzbina.";
			return retVal;
		}

		retVal.success = true;
		return retVal;
	}
	
	@GET
	@Path("/orders/deliverer")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Order> checkForDeliverer(@Context HttpServletRequest request) {
		boolean hasOrder = false;
		ServiceResponse<Order> retVal = new ServiceResponse<Order>();
		HttpSession session = request.getSession(true);
		User deliverer = (User) session.getAttribute("loggedUser");
		
		for (Order o : InitData.getInstance().getDataStore().getOrders()) {
			if (!o.isDeleted()) {
				if (o.getStatus().equals(OrderStatus.inProgress)) {
					if(o.getDeliverer().getUsername().equals(deliverer.getUsername())) {
						retVal.success = false;
						return retVal;
					}

				}
			}

		}
		
		retVal.success = true;
		return retVal;

	}

	@GET
	@Path("/orders/{orderId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Order> getOrderById(@PathParam("orderId") String orderId) {
		ServiceResponse<Order> retVal = new ServiceResponse<Order>();

		for (Order o : InitData.getInstance().getDataStore().getOrders()) {
			if (!o.isDeleted()) {
				if (o.getId().equals(orderId)) {
					retVal.data = o;
					retVal.success = true;
					return retVal;
				}
			}

		}

		retVal.success = false;
		retVal.message = "Nema tog ID-a ordera.";
		return retVal;
	}

	@PUT
	@Path("/orders/{orderId}/{status}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Order> changeOrderStatus(@PathParam("orderId") String orderId,
			@PathParam("status") String status, @Context HttpServletRequest request) {
		ServiceResponse<Order> retVal = getOrderById(orderId);
		ArrayList<Order> orderList = InitData.getInstance().getDataStore().getOrders();
		Order order = retVal.data;
		HttpSession session = request.getSession(true);
		User deliverer = (User) session.getAttribute("loggedUser");

		
		switch (status) {
		case "ordered":
			order.setStatus(OrderStatus.ordered);
			break;
		case "inProgress": {
			order.setStatus(OrderStatus.inProgress);
			order.setDeliverer(deliverer);
			//deliverer.getDeliveryOrders().add(order);
			break;
		}
		case "canceled":
			order.setStatus(OrderStatus.canceled);
			break;
		case "delivered": {
			order.setStatus(OrderStatus.delivered);
			//deliverer.getOrders().remove(order);
			break;
		}

		default:
			retVal.success = false;
			retVal.message = "Greska.";
			return retVal;
		}

		for (Order o : orderList) {
			if (!o.isDeleted()) {
				if (o.getId().equals(orderId)) {
					orderList.set(orderList.indexOf(o), order);
					InitData.getInstance().serialize();
					retVal.success = true;
					return retVal;
				}
			}

		}

		retVal.success = false;
		retVal.message = "Greska.";
		return retVal;
	}
	
	@GET
	@Path("/orders/users/{username}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Order>> getCustomerOrders(@PathParam ("username") String username) {
		ServiceResponse<ArrayList<Order>> retVal = new ServiceResponse<ArrayList<Order>>();
		ArrayList<Order> orders = new ArrayList<Order>();
		
		for(Order o : InitData.getInstance().getDataStore().getOrders()) {
			if(!o.isDeleted()) {
				if(o.getBuyer().getUsername().equals(username)) {
					orders.add(o);
				}
			}
		}
		
		retVal.data = orders;
		
		if(orders.isEmpty()) {
			retVal.success = false;
			retVal.message = "Lista prazna.";
			return retVal;
		}
				
		retVal.success = true;
		return retVal;
	}
	
	@POST
	@Path("/orders/{buyer}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<OrderDTO> placeOrderAdmin(OrderDTO orderDTO, @PathParam("buyer") String username) {
		ServiceResponse<OrderDTO> retVal = new ServiceResponse<OrderDTO>();
		if (orderDTO.getArticles().isEmpty()) {
			retVal.success = false;
			retVal.message = "Morate izabrati bar jedan artikal.";
			return retVal;
		}
		ServiceResponse<Restaurant> response = rs.getRestaurant(orderDTO.getRestaurant());
		Restaurant restaurant = response.data;
		Order order = new Order();
		User user = new User();
		
		for(User u : InitData.getInstance().getDataStore().getUsers()) {
			if(u.getUsername().equals(username)) {
				user = u;
			}
		}
		
		order.setExtraInfo(orderDTO.getNotes());
		order.setStatus(OrderStatus.ordered);
		order.setDate(new Date());
		order.setDeliverer(null);
		order.setBuyer(user);

		for (Article a : restaurant.getArticles()) {
			if (!a.isDeleted()) {
				for (int i = 0; i < orderDTO.getArticles().size(); i++) {
					if (a.getName().equals(orderDTO.getArticles().get(i))) {
						order.getItems().add(a);
					}
				}

			}
		}
		order.setId(generateString(10));
		InitData.getInstance().getDataStore().getOrders().add(order);
		InitData.getInstance().serialize();
		retVal.success = true;
		return retVal;
	}
	
	@DELETE
	@Path("/orders/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Order> deleteOrder(@PathParam("orderId") String orderId) {
		ServiceResponse<Order> retVal = new ServiceResponse<Order>();
		
		retVal = getOrderById(orderId);
		if(retVal.success) {
			Order order = retVal.data;
			User deliverer = order.getDeliverer();
			if(deliverer != null) {
				deliverer.getVehicle().setInUse(false);
				order.setDeliverer(null);
			}
			
			order.setDeleted(true);
			InitData.getInstance().serialize();
			return retVal;
		}
		
		
		retVal.success = false;
		return retVal;
	}
	
	@PUT
	@Path("/cancelOrders/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Order> cancelOrder(@PathParam("orderId") String orderId) {
		ServiceResponse<Order> retVal = new ServiceResponse<Order>();
		
		retVal = getOrderById(orderId);
		if(retVal.success) {
			Order order = retVal.data;
			if(!order.getStatus().equals(OrderStatus.delivered)) {
				User deliverer = order.getDeliverer();
				if(deliverer != null) {
					deliverer.getVehicle().setInUse(false);
					order.setDeliverer(null);
				}
				
				order.setStatus(OrderStatus.canceled);
				InitData.getInstance().serialize();
				return retVal;
			}
			
		}
		
		
		retVal.success = false;
		return retVal;
	}

}
