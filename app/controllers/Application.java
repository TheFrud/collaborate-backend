package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	public static Result preflight(String all) {
		System.out.println(request().getHeader("Origin"));
		response().setHeader("Access-Control-Allow-Origin", request().getHeader("Origin"));
		// response().setHeader("Allow", "*");
		response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
		response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, cookie, X-AUTH-TOKEN, authToken");
		response().setHeader("Access-Control-Allow-Credentials", "true"); 
		return ok();
	}

	@With(ExtraController.class)
    public static Result index() {
		Logger.info("Now in Application method.");
        return ok(index.render("Your new application is ready."));
    }
	
	public static Result test() {
		return ok("");
	}
}