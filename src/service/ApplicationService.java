package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import domain.ArticleType;
import domain.Order;
import domain.Restaurant;
import domain.User;
import domain.UserType;
import domain.Vehicle;

@Path("/api")
public class ApplicationService {
	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	@Context
	Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {

		return "Hello Jersey";
	}

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<User> login(User user, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ServiceResponse<User> retVal = new ServiceResponse<User>();
		if (user.getUsername().equals("") || user.getPassword().equals("")) {
			retVal.success = false;
			retVal.message = "Niste uneli sve podatke.";
			return retVal;
		}

		for (User u : InitData.getInstance().getDataStore().getUsers()) {
			if (u.getUsername().equals(user.getUsername())) {
				if (u.getPassword().equals(user.getPassword())) {
					session.setAttribute("loggedUser", u);
					retVal.success = true;
					retVal.message = "Uspesno logovanje.";
					retVal.data = u;
					return retVal;
				} else {
					retVal.success = false;
					retVal.message = "Pogresna lozinka.";
				}
			}
		}

		retVal.success = false;
		retVal.message = "Korisnik ne postoji.";
		return retVal;

	}

	@GET
	@Path("/sessions")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<User> getSession(@Context HttpServletRequest request) {
		ServiceResponse<User> retVal = new ServiceResponse<User>();
		HttpSession session = request.getSession(true);
		if (session != null) {
			if (session.getAttribute("loggedUser") == null) {
				retVal.success = false;
				return retVal;
			}
			retVal.authenticatedUser = (User) session.getAttribute("loggedUser");
			retVal.data = (User) session.getAttribute("loggedUser");
			retVal.success = true;
			return retVal;
		}

		retVal.success = false;
		return retVal;

	}

	@POST
	@Path("/sessions")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<User> logout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		ServiceResponse<User> retVal = new ServiceResponse<User>();
		if (session != null) {
			session.invalidate();
			retVal.success = true;
			retVal.message = "Odjavljeni ste.";
			return retVal;
		}

		retVal.success = false;
		retVal.message = "Greska";

		return retVal;
	}

	@POST
	@Path("/register")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<User> register(User user) {
		ServiceResponse<User> retVal = new ServiceResponse<User>();
		if (user.getUsername().equals("") || user.getPassword().equals("") || user.getName().equals("")
				|| user.getLastName().equals("") || user.getEmail().equals("") || user.getPhoneNum().equals("")) {
			retVal.success = false;
			retVal.message = "Niste uneli sve podatke.";
			return retVal;
		}
		ArrayList<User> userList = new ArrayList<User>();
		for (User u : userList) {
			if (u.getUsername().equals(user.getUsername())) {
				retVal.message = "Korisnik vec postoji.";
				retVal.success = false;
				return retVal;
			}
		}
		user.setType(UserType.buyer);
		user.setRegistrationDate(new Date());
		InitData.getInstance().getDataStore().getUsers().add(user);
		InitData.getInstance().serialize();
		retVal.data = user;
		retVal.message = "Registrovali ste se!";
		retVal.success = true;
		return retVal;

	}

	@GET
	@Path("/users")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<User>> getUsers() {
		ServiceResponse<ArrayList<User>> retVal = new ServiceResponse<ArrayList<User>>();
		ArrayList<User> userList = new ArrayList<User>();
		for (User u : InitData.getInstance().getDataStore().getUsers()) {
			if (u.getType() != UserType.administrator) {
				userList.add(u);
			}
		}
		retVal.data = userList;
		retVal.success = true;
		return retVal;
	}

	@PUT
	@Path("/users/{username}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<User> editUserType(@PathParam("username") String username) {
		ServiceResponse<User> retVal = new ServiceResponse<User>();
		ArrayList<User> userList = InitData.getInstance().getDataStore().getUsers();
		for (User u : userList) {
			if (u.getUsername().equals(username)) {
				if (u.getType() == UserType.deliverer) {
					u.setType(UserType.buyer);
					InitData.getInstance().serialize();
					retVal.success = true;
					retVal.data = u;
					return retVal;
				} else {
					u.setType(UserType.deliverer);
					retVal.success = true;
					retVal.data = u;
					return retVal;
				}
			}
		}

		retVal.success = false;
		retVal.message = "Korisnik nije pronadjen.";
		return retVal;
	}

	@GET
	@Path("/vehicles")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Vehicle>> getVehicles() {
		ServiceResponse<ArrayList<Vehicle>> retValList = new ServiceResponse<ArrayList<Vehicle>>();
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		for (Vehicle v : InitData.getInstance().getDataStore().getVehicles()) {
			if (!v.isDeleted()) {
				vehicles.add(v);
			}
		}

		retValList.data = vehicles;

		if (vehicles.isEmpty()) {
			retValList.success = false;
			retValList.message = "Nije pronadjeno ni jedno vozilo.";
			return retValList;
		}
		retValList.success = true;
		return retValList;
	}

	@GET
	@Path("/vehicles/{regNum}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> getVehicle(@PathParam("regNum") String regNum) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		ArrayList<Vehicle> vehicles = InitData.getInstance().getDataStore().getVehicles();

		for (Vehicle v : vehicles) {
			if (v.getRegistrationNum().equals(regNum)) {
				if (!v.isDeleted()) {
					retVal.success = true;
					retVal.data = v;
					return retVal;
				}
			}
		}

		retVal.success = false;
		retVal.message = "Vozilo nije pronadjeno.";
		return retVal;
	}

	@POST
	@Path("/vehicles")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> addVehicle(Vehicle vehicle) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		ArrayList<Vehicle> vehicles = InitData.getInstance().getDataStore().getVehicles();

		for (Vehicle v : vehicles) {
			if (v.getRegistrationNum().equals(vehicle.getRegistrationNum())) {
				retVal.success = false;
				retVal.message = "Vozilo vec postoji.";
				return retVal;
			}
		}

		InitData.getInstance().getDataStore().getVehicles().add(vehicle);
		InitData.getInstance().serialize();
		retVal.success = true;
		retVal.data = vehicle;
		retVal.message = "Vozilo uspesno dodato.";
		return retVal;
	}

	@DELETE
	@Path("/vehicles/{regNum}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> deleteVehicle(@PathParam("regNum") String regNum) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		ArrayList<Vehicle> vehicles = InitData.getInstance().getDataStore().getVehicles();

		for (Vehicle v : vehicles) {
			if (v.getRegistrationNum().equals(regNum)) {
				if (!v.isDeleted()) {
					retVal.success = true;
					v.setDeleted(true);
					InitData.getInstance().serialize();
					return retVal;
				}
			}
		}

		retVal.success = false;
		retVal.message = "Vozilo nije pronadjeno.";
		return retVal;
	}

	@PUT
	@Path("/vehicles/{oldRegNum}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> addVehicle(Vehicle vehicle, @PathParam("oldRegNum") String oldRegNum) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		ArrayList<Vehicle> vehicles = InitData.getInstance().getDataStore().getVehicles();

		for (Vehicle v : vehicles) {
			if (v.getRegistrationNum().equals(oldRegNum)) {
				vehicles.set(vehicles.indexOf(v), vehicle);
				InitData.getInstance().serialize();
				retVal.success = true;
				retVal.data = vehicle;
				return retVal;
			}
		}

		retVal.message = "Greska!";
		retVal.success = false;
		retVal.data = vehicle;
		return retVal;
	}

	@PUT
	@Path("/vehicles/{regNum}/{status}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> changeVehicleStatus(@PathParam("regNum") String regNum,
			@PathParam("status") String status, @Context HttpServletRequest request) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		HttpSession session = request.getSession(true);
		User deliverer = (User) session.getAttribute("loggedUser");
		ArrayList<Vehicle> vehicles = InitData.getInstance().getDataStore().getVehicles();
		ServiceResponse<Vehicle> res = getVehicle(regNum);
		Vehicle veh = res.data;
		if (status.equals("inUse")) {
			veh.setInUse(true);
			deliverer.setVehicle(veh);
		} else {
			veh.setInUse(false);
			deliverer.setVehicle(null);
		}

		for (Vehicle v : vehicles) {
			if (!v.isDeleted()) {
				if (v.getRegistrationNum().equals(veh.getRegistrationNum())) {
					vehicles.set(vehicles.indexOf(v), veh);
					InitData.getInstance().serialize();
					retVal.success = true;
					retVal.data = veh;
					return retVal;
				}
			}
		}

		retVal.message = "Greska!";
		retVal.success = false;
		retVal.data = veh;
		return retVal;
	}

	@GET
	@Path("/users/delivererVehichle")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Vehicle> getDelVeh(@Context HttpServletRequest request) {
		ServiceResponse<Vehicle> retVal = new ServiceResponse<Vehicle>();
		HttpSession session = request.getSession(true);
		User deliverer = (User) session.getAttribute("loggedUser");
		if (deliverer.getVehicle() == null) {
			retVal.success = false;
			retVal.message = "Nema vozilo dostavljac.";
			return retVal;
		}

		retVal.success = true;
		retVal.data = deliverer.getVehicle();
		return retVal;
	}

	@GET
	@Path("/favArticles")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Article>> getFavArticles() {
		ServiceResponse<ArrayList<Article>> retVal = new ServiceResponse<ArrayList<Article>>();
		Map<Article, Integer> food = new HashMap<Article, Integer>();
		Map<Article, Integer> drinks = new HashMap<Article, Integer>();

		for (Restaurant r : InitData.getInstance().getDataStore().getRestaurants()) {
			for (Article a : r.getArticles()) {
				if(!a.isDeleted()) {
					if (a.getType().equals(ArticleType.food)) {
						food.put(a, 0);
					} else {
						drinks.put(a, 0);
					}
				}
				
			}
		}

		for (Order o : InitData.getInstance().getDataStore().getOrders()) {
			if (!o.isDeleted()) {
				for (Article item : o.getItems()) {
					if (item.getType().equals(ArticleType.food)) {
						for (Article article : food.keySet()) {
							if (item.getName().equals(article.getName()) && item.getType().equals(ArticleType.food)) {
								food.replace(article, food.get(article) + 1);
							}
						}
					} else {
						for (Article article : drinks.keySet()) {
							if (item.getName().equals(article.getName()) && item.getType().equals(ArticleType.drink)) {
								drinks.replace(article, drinks.get(article) + 1);
							}
						}
					}
				}
			}

		}

		Map<Article, Integer> sortedFood = food.entrySet().stream()
				.sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		Map<Article, Integer> sortedDrinks = drinks.entrySet().stream()
				.sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		ArrayList<Article> data = new ArrayList<Article>();

		int i = 0;
		for (Article a : sortedFood.keySet()) {
			if (i < 10) {
				data.add(a);
				i++;
			} else {
				break;
			}
		}
		
		int j = 0;
		for (Article a : sortedDrinks.keySet()) {
			if (j < 10) {
				data.add(a);
				j++;
			} else {
				break;
			}
		}
		
		retVal.data = data;
		retVal.success = true;
		return retVal;
	}

}
