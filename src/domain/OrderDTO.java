package domain;

import java.util.ArrayList;

public class OrderDTO {

	private ArrayList<String> articles;
	private String restaurant;
	private String notes;

	public OrderDTO() {

	}

	public ArrayList<String> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<String> articles) {
		this.articles = articles;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(String restaurant) {
		this.restaurant = restaurant;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
