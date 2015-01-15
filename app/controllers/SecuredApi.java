package controllers;

import play.Logger;
import play.mvc.Result;
import views.html.index;
import play.mvc.Security;
import play.mvc.*;

@Security.Authenticated(Secured.class)
public class SecuredApi extends Controller{

    public static Result test() {
		Logger.info("Tried to do something.");
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result getHello() {
    	Logger.info("Hello method");
    	return ok("Hello!");
    }
    
}
