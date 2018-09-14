package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Order {

	private String id;
	private ArrayList<Article> items = new ArrayList<Article>();
	private Date date;
	private User buyer;
	private User deliverer;
	private OrderStatus status;
	private String extraInfo;
	private boolean deleted = false;

	public Order() {

	}

	public ArrayList<Article> getItems() {
		return items;
	}

	public void setItems(ArrayList<Article> items) {
		this.items = items;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getBuyer() {
		return buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public User getDeliverer() {
		return deliverer;
	}

	public void setDeliverer(User deliverer) {
		this.deliverer = deliverer;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
