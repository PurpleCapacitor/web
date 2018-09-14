package domain;

import java.util.ArrayList;
import java.util.Date;

public class User {

	private String username;
	private String password;
	private String name;
	private String lastName;
	private UserType type;
	private String phoneNum;
	private String email;
	private Date registrationDate;


	// kupac
	private ArrayList<Order> orders;
	private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	// dostavljac
	private Vehicle vehicle;
	private ArrayList<Order> deliveryOrders = new ArrayList<Order>();

	public User() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

	public ArrayList<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(ArrayList<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public ArrayList<Order> getDeliveryOrders() {
		return deliveryOrders;
	}

	public void setDeliveryOrders(ArrayList<Order> deliveryOrders) {
		this.deliveryOrders = deliveryOrders;
	}

}
