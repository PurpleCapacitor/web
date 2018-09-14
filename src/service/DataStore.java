package service;

import java.util.ArrayList;

import domain.Order;
import domain.Restaurant;
import domain.User;
import domain.Vehicle;

public class DataStore {

	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	private ArrayList<Order> orders = new ArrayList<Order>();

	public DataStore() {

	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public ArrayList<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(ArrayList<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(ArrayList<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

}
