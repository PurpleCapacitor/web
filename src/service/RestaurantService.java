package service;

import java.util.ArrayList;

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
import domain.Restaurant;
import domain.User;

@Path("/api")
public class RestaurantService {

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
	@Path("/test3")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {

		return "Hello Jersey";
	}

	@GET
	@Path("/restaurants")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Restaurant>> getRestaurants() {
		ServiceResponse<ArrayList<Restaurant>> retValList = new ServiceResponse<ArrayList<Restaurant>>();
		ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
		
		for(Restaurant r : InitData.getInstance().getDataStore().getRestaurants()) {
			if(!r.isDeleted()) {
				restaurantList.add(r);
			}
		}
		retValList.data = restaurantList;
		
		if(restaurantList.isEmpty()) {
			retValList.success = false;
			return retValList;
		}
		retValList.success = true;
		return retValList;
	}

	@GET
	@Path("/restaurants/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Restaurant> getRestaurant(@PathParam("name") String name) {
		ArrayList<Restaurant> restaurantList = InitData.getInstance().getDataStore().getRestaurants();
		ServiceResponse<Restaurant> retVal = new ServiceResponse<>();

		for (Restaurant r : restaurantList) {
			if (r.getName().equals(name)) {
				if (!r.isDeleted()) {
					retVal.data = r;
					retVal.success = true;
					retVal.message = "Restoran pronadjen.";
					return retVal;
				}
			}
		}

		retVal.message = "Restoran nije pronadjen.";
		retVal.success = false;

		return retVal;
	}
	
	@GET
	@Path("/restaurants/cat/{type}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Restaurant>> getRestaurantType(@PathParam("type") String type) {
		ArrayList<Restaurant> restaurantList = InitData.getInstance().getDataStore().getRestaurants();
		ArrayList<Restaurant> restaurantListFound = new ArrayList<Restaurant>();
		ServiceResponse<ArrayList<Restaurant>> retVal = new ServiceResponse<ArrayList<Restaurant>>();

		for (Restaurant r : restaurantList) {
			if (r.getRestaurantType().toString().equals(type)) {
				if (!r.isDeleted()) {
					restaurantListFound.add(r);
				}
			}
		}
		
		if(restaurantListFound.isEmpty()) {
			retVal.message = "Restorani nisu pronadjeni.";
			retVal.success = false;
			return retVal;
		}

		
		retVal.success = true;
		retVal.data = restaurantListFound;
		return retVal;
	}

	@DELETE
	@Path("/restaurants/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Restaurant> deleteRestaurant(@PathParam("name") String name) {
		ArrayList<Restaurant> res = InitData.getInstance().getDataStore().getRestaurants();

		for (Restaurant r : res) {
			if (r.getName().equals(name)) {
				r.setDeleted(true);
				InitData.getInstance().serialize();
			}
		}

		return null;
	}

	@POST
	@Path("/restaurants/")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Restaurant> addRestaurant(Restaurant r) {
		ArrayList<Restaurant> restaurantList = InitData.getInstance().getDataStore().getRestaurants();
		ServiceResponse<Restaurant> retVal = new ServiceResponse<>();

		for (Restaurant res : restaurantList) {
			if (res.getName().equals(r.getName())) {
				retVal.message = "Restoran vec postoji.";
				retVal.success = false;
				return retVal;

			}
		}
		r.setArticles(new ArrayList<Article>());
		InitData.getInstance().getDataStore().getRestaurants().add(r);
		InitData.getInstance().serialize();
		retVal.message = "Restoran dodat.";
		retVal.success = true;
		retVal.data = r;
		return retVal;
	}

	@PUT
	@Path("/restaurants/{oldName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Restaurant> editRestaurant(Restaurant r, @PathParam("oldName") String oldName) {
		ArrayList<Restaurant> restaurantList = InitData.getInstance().getDataStore().getRestaurants();
		ServiceResponse<Restaurant> retVal = new ServiceResponse<Restaurant>();

		for (Restaurant res : restaurantList) {
			if (res.getName().equals(oldName)) {
				r.setArticles(res.getArticles());
				restaurantList.set(restaurantList.indexOf(res), r);
				InitData.getInstance().getDataStore().setRestaurants(restaurantList);
				InitData.getInstance().serialize();
				retVal.success = true;
				retVal.data = r;
				return retVal;
			}
		}

		retVal.message = "Greska!";
		retVal.success = false;
		retVal.data = r;
		return retVal;
	}

	// Artikli

	@GET
	@Path("/articles/{restaurant}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Article>> getArticles(@PathParam("restaurant") String resName) {
		ServiceResponse<ArrayList<Article>> retValList = new ServiceResponse<ArrayList<Article>>();
		ArrayList<Restaurant> resList = InitData.getInstance().getDataStore().getRestaurants();

		for (Restaurant r : resList) {
			if (r.getName().equals(resName)) {
				if(!r.isDeleted()) {
					if (r.getArticles().isEmpty()) {
						retValList.success = false;
						retValList.message = "Nema artikala.";
						return retValList;
					} else {
						retValList.data = r.getArticles();
						retValList.success = true;
						return retValList;
					}
				} else {
					retValList.success = false;
					retValList.message = "Nema artikala.";
					return retValList;
				}

			}

		}
		retValList.success = false;
		retValList.message = "Restoran nije pronadjen.";
		return retValList;

	}

	@GET
	@Path("/articles/{restaurant}/{articleName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Article> getArticle(@PathParam("restaurant") String resName,
			@PathParam("articleName") String articleName) {
		ServiceResponse<Article> retVal = new ServiceResponse<Article>();
		ArrayList<Restaurant> resList = InitData.getInstance().getDataStore().getRestaurants();

		for (Restaurant r : resList) {
			if (r.getName().equals(resName)) {
				if (r.getArticles().isEmpty()) {
					retVal.success = false;
					retVal.message = "Restoran nema artikala.";
					return retVal;
				} else {
					ArrayList<Article> articleList = r.getArticles();
					for (Article a : articleList) {
						if (a.getName().equals(articleName)) {
							retVal.data = a;
							retVal.success = true;
							return retVal;
						}
					}

					retVal.success = false;
					retVal.message = "Artikal nije pronadjen.";
					return retVal;
				}

			}

		}
		retVal.success = false;
		retVal.message = "Restoran nije pronadjen.";
		return retVal;
	}

	@DELETE
	@Path("/articles/{restaurant}/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Article> deleteArticle(@PathParam("name") String name, @PathParam("restaurant") String res) {
		ServiceResponse<Article> retVal = new ServiceResponse<Article>();
		ServiceResponse<Restaurant> methodRes = getRestaurant(res);
		Restaurant restaurant;
		if (methodRes.success) {
			restaurant = methodRes.data;
			for (Article ar : restaurant.getArticles()) {
				if (ar.getName().equals(name)) {
					ar.setDeleted(true);
					InitData.getInstance().serialize();
					retVal.success = true;
					return retVal;
				}
			}

			retVal.message = "Artikal nije pronadjen";
			retVal.success = false;
			return retVal;

		} else {
			retVal.message = "Restoran nije pronadjen";
			retVal.success = false;
			return retVal;
		}

	}

	@POST
	@Path("/articles/{restaurant}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Article> addArticle(Article article, @PathParam("restaurant") String res) {
		ServiceResponse<Article> retVal = new ServiceResponse<Article>();
		ServiceResponse<Restaurant> methodRes = getRestaurant(res);
		Restaurant restaurant;
		if (methodRes.success) {
			restaurant = methodRes.data;
			for (Article a : restaurant.getArticles()) {
				if (a.getName().equals(article.getName())) {
					if (!a.isDeleted()) {
						retVal.success = false;
						retVal.message = "Artikal vec postoji.";
						return retVal;
					}
				}
			}

			restaurant.getArticles().add(article);
			article.setRestaurant(res);
			InitData.getInstance().serialize();
			retVal.success = true;
			retVal.data = article;
			return retVal;

		} else {
			retVal.message = "Restoran nije pronadjen";
			retVal.success = false;
			return retVal;
		}

	}

	@PUT
	@Path("/articles/{restaurant}/{oldName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<Article> editArticle(Article article, @PathParam("restaurant") String res,
			@PathParam("oldName") String oldName) {
		ServiceResponse<Article> retVal = new ServiceResponse<Article>();
		ServiceResponse<Restaurant> methodRes = getRestaurant(res);
		Restaurant restaurant;
		if (methodRes.success) {
			restaurant = methodRes.data;
			for (Article a : restaurant.getArticles()) {
				if (a.getName().equals(oldName)) {
					if (!a.isDeleted()) {
						article.setRestaurant(restaurant.getName());
						restaurant.getArticles().set(restaurant.getArticles().indexOf(a), article);
						InitData.getInstance().serialize();
						retVal.success = true;
						return retVal;
					}

				}
			}
			retVal.success = false;
			retVal.message = "Artikal nije pronadjen.";
			return retVal;

		} else {
			retVal.message = "Restoran nije pronadjen";
			retVal.success = false;
			return retVal;
		}

	}

	@POST
	@Path("/searchRestaurants")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Restaurant>> getSearchRes(Restaurant res) {
		ServiceResponse<ArrayList<Restaurant>> retVal = new ServiceResponse<ArrayList<Restaurant>>();
		ArrayList<Restaurant> resList = InitData.getInstance().getDataStore().getRestaurants();
		ArrayList<Restaurant> resListFound = new ArrayList<Restaurant>();

		if ((res.getName().isEmpty()) && (res.getAddress().isEmpty()) && (res.getRestaurantType() == null)) {
			retVal.success = false;
			retVal.message = "Morate uneti neki od podataka za pretragu.";
			return retVal;
		}

		for (Restaurant r : resList) {
			if (!r.isDeleted()) {
				if ((r.getName().toLowerCase().contains(res.getName().toLowerCase()))
						&& (r.getAddress().toLowerCase().contains(res.getAddress().toLowerCase()))) {
					if (r.getRestaurantType().equals(res.getRestaurantType())) {
						resListFound.add(r);
					} else if (res.getRestaurantType() == null) {
						resListFound.add(r);
					}

				}
			}

		}
		if (resListFound.isEmpty()) {
			retVal.data = resListFound;
			retVal.success = false;
			retVal.message = "Nije pronadjen ni jedan restoran.";
			return retVal;
		}
		retVal.data = resListFound;
		retVal.success = true;
		return retVal;
	}

	@POST
	@Path("/searchArticles/{resName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public ServiceResponse<ArrayList<Article>> getSearchAr(Article article, @PathParam("resName") String resName) {
		ServiceResponse<ArrayList<Article>> retVal = new ServiceResponse<ArrayList<Article>>();
		ArrayList<Restaurant> resList = InitData.getInstance().getDataStore().getRestaurants();
		Restaurant restaurant = new Restaurant();
		ArrayList<Article> articlesFound = new ArrayList<Article>();

		if ((article.getName().isEmpty()) && (article.getType() == null) && (article.getPricePerUnit().isEmpty())) {
			retVal.success = false;
			retVal.message = "Morate uneti neki od podataka za pretragu.";
			return retVal;
		}

		for (Restaurant r : resList) {
			if (!r.isDeleted()) {
				if (r.getName().equals(resName)) {
					restaurant = r;
				}
			}

		}

		for (Article a : restaurant.getArticles()) {
			if (!a.isDeleted()) {
				if (!article.getPricePerUnit().isEmpty()) {
					if (a.getName().toLowerCase().contains(article.getName().toLowerCase())
							&& a.getPricePerUnit().toLowerCase().equals(article.getPricePerUnit().toLowerCase())) {
						if (a.getType().equals(article.getType())) {
							articlesFound.add(a);
						} else if (article.getType() == null) {
							articlesFound.add(a);
						}

					}
				} else if (a.getName().toLowerCase().contains(article.getName().toLowerCase())) {
					if (a.getType().equals(article.getType())) {
						articlesFound.add(a);
					} else if (article.getType() == null) {
						articlesFound.add(a);
					}

				}
			}

		}

		if (articlesFound.isEmpty()) {
			retVal.data = articlesFound;
			retVal.success = false;
			retVal.message = "Nije pronadjen ni jedan artikal.";
			return retVal;
		}

		retVal.data = articlesFound;
		retVal.success = true;
		return retVal;
	}
	
	@POST
	@Path("/users/restaurants/{resName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public  ServiceResponse<Restaurant> addFavResturant(@Context HttpServletRequest request, @PathParam("resName") String resName) {
		ServiceResponse<Restaurant> retVal = new ServiceResponse<Restaurant>();
		ServiceResponse<Restaurant> res = getRestaurant(resName);
		Restaurant restaurant = res.data;
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("loggedUser");	
		for(Restaurant r : user.getRestaurants()) {
			if(r.getName().equals(resName)) {
				retVal.success = false;
				retVal.message = "Restoran je vec omiljeni.";
				return retVal;
			}
		}
		user.getRestaurants().add(restaurant);
		InitData.getInstance().serialize();
		retVal.success = true;
		return retVal;
			
		
	}
	

	@GET
	@Path("/users/restaurants/")
	@Produces({ MediaType.APPLICATION_JSON })
	public  ServiceResponse<ArrayList<Restaurant>> getFavResturants(@Context HttpServletRequest request) {
		ServiceResponse<ArrayList<Restaurant>> retVal = new ServiceResponse<ArrayList<Restaurant>>();
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("loggedUser");		
		retVal.data = user.getRestaurants();
		
		if(retVal.data.isEmpty()) {
			retVal.success = false;
			retVal.message = "Nema restorana.";
			return retVal; 
		}
		retVal.success = true;
		return retVal;
			
		
	}
	
	@GET
	@Path("/users/restaurants/{resName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public  ServiceResponse<Restaurant> checkIfFavResturants(@Context HttpServletRequest request, @PathParam("resName") String resName) {
		ServiceResponse<Restaurant> retVal = new ServiceResponse<Restaurant>();
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("loggedUser");	
		
		for(Restaurant r : user.getRestaurants()) {
			if(r.getName().equals(resName)) {
				retVal.success = true;
				return retVal;
			}
		}
		
		retVal.success = false;
		return retVal;
			
		
	}
}
