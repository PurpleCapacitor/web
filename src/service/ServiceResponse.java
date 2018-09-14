package service;

import domain.User;

public class ServiceResponse<T> {
	public boolean success;
	public String message;
	public T data;
	public User authenticatedUser;
}
