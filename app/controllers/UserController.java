package controllers;

import models.Userr;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

@Security.Authenticated(Secured.class)
public class UserController extends Controller{
	
	public static Result getCurrentUser() {
		Logger.info("I metoden");
		Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	return ok(toJson(user));
	}

}
