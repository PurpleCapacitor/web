package service;

import java.time.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.UserType;
import domain.Vehicle;
import domain.VehicleType;
import domain.Article;
import domain.ArticleType;
import domain.Order;
import domain.OrderStatus;
import domain.Restaurant;
import domain.RestaurantType;
import domain.User;

public class InitData {

	private static InitData instance = null;
	private String rootPath;
	private DataStore dataStore;
	private OrderService orderService = new OrderService();

	public InitData() {

	}

	public static InitData getInstance() {
		if (instance == null) {
			instance = new InitData();
		}
		return instance;
	}
	

	public void Initialize(String path) {
		this.rootPath = path;
		System.out.println(this.rootPath);
		this.deserialize();
		User u1 = new User();
		User deliverer = new User();
		ArrayList<Article> arList = new ArrayList<Article>();
		if (this.dataStore.getUsers().isEmpty()) {
			User admin = new User();
			admin.setUsername("a");
			admin.setName("Manda");
			admin.setEmail("a");
			admin.setPassword("a");
			admin.setLastName("Manda");
			admin.setPhoneNum("123");
			admin.setRegistrationDate(new Date());
			admin.setType(UserType.administrator);
			this.dataStore.getUsers().add(admin);

			
			u1.setUsername("b");
			u1.setName("Da");
			u1.setEmail("b");
			u1.setPassword("b");
			u1.setLastName("Da");
			u1.setPhoneNum("123");
			u1.setRegistrationDate(new Date());
			u1.setType(UserType.buyer);
			this.dataStore.getUsers().add(u1);

			
			deliverer.setUsername("c");
			deliverer.setName("Mihailo");
			deliverer.setEmail("d1@d1");
			deliverer.setPassword("c");
			deliverer.setLastName("Mandic");
			deliverer.setPhoneNum("123");
			deliverer.setRegistrationDate(new Date());
			deliverer.setType(UserType.deliverer);
			this.dataStore.getUsers().add(deliverer);

			this.serialize();
		}

		if (this.dataStore.getRestaurants().isEmpty()) {
			Article a1 = new Article();
			a1.setName("Fanta");
			a1.setPricePerUnit("150");
			a1.setType(ArticleType.drink);
			a1.setDescription("Osvezavajuće piće!");
			a1.setRestaurant("Res1");
			
			Article a2 = new Article();
			a2.setName("Kola");
			a2.setPricePerUnit("140");
			a2.setType(ArticleType.drink);
			a2.setDescription("Osvezavajuće piće!");
			a2.setRestaurant("Res1");
			
			Article a3 = new Article();
			a3.setName("Griz");
			a3.setPricePerUnit("140");
			a3.setType(ArticleType.food);
			a3.setDescription("Dijetalno");
			a3.setRestaurant("Res1");
			
			Article a4 = new Article();
			a4.setName("Kolac");
			a4.setPricePerUnit("240");
			a4.setType(ArticleType.food);
			a4.setDescription("Odlican");
			a4.setRestaurant("Res1");
			arList.add(a2);
			arList.add(a4);
			
			Restaurant r1 = new Restaurant();
			r1.setAddress("Adresa");
			r1.setDeleted(false);
			r1.setName("Kod Kebe");
			r1.setRestaurantType(RestaurantType.domestic);
			a1.setRestaurant(r1.getName());
			r1.getArticles().add(a1);
			r1.getArticles().add(a2);
			r1.getArticles().add(a3);
			r1.getArticles().add(a4);
			this.dataStore.getRestaurants().add(r1);

			Restaurant r2 = new Restaurant();
			r2.setAddress("Adresa");
			r2.setDeleted(false);
			r2.setName("Pizzomizzo");
			r2.setRestaurantType(RestaurantType.pizzeria);
			this.dataStore.getRestaurants().add(r2);

			this.serialize();
		}

		if (this.dataStore.getVehicles().isEmpty()) {
			Vehicle v1 = new Vehicle();
			v1.setExtraInfo("none");
			v1.setMake("Fiat");
			v1.setModel("Punto");
			v1.setProductionYear(2002);
			v1.setRegistrationNum("NS001AA");
			v1.setVehicleType(VehicleType.car);

			Vehicle v2 = new Vehicle();
			v2.setExtraInfo("Udoban");
			v2.setMake("Mercedes-Benz");
			v2.setModel("C klasa");
			v2.setProductionYear(2005);
			v2.setRegistrationNum("NS002AA");
			v2.setVehicleType(VehicleType.car);

			this.dataStore.getVehicles().add(v1);
			this.dataStore.getVehicles().add(v2);
			this.serialize();
		}
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public void serialize() {
		ObjectMapper mapper = new ObjectMapper();
		DataStore dataStore = instance.dataStore;
		String data = "";
		try {
			data = mapper.writeValueAsString(dataStore);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		if (!data.equals("")) {
			File file = new File(this.rootPath + "/podaci.json");
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(file);
				fileWriter.write(data);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void deserialize() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			DataStore dataStore = mapper.readValue(new File(this.rootPath + "/podaci.json"), DataStore.class);
			instance.dataStore = dataStore;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
