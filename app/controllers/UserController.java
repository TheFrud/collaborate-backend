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
	
    public static Result editUser() {
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	Logger.info(String.valueOf(user.id));
    	Logger.info(user.bio);
    	Logger.info("Edit user method");
    	JsonNode json = request().body().asJson();
    	String bio = json.findPath("bio").textValue();
    	Logger.info(bio);
    	Logger.info(user.fullName);
    	user.setBio(bio);
    	user.save();
    	return ok("User edited.");
    }
    
    public static Result getUser() {
    	JsonNode json = request().body().asJson();
    	Long userId = json.findPath("userId").asLong();	
    	Userr user = Userr.find.byId(userId);
    	return ok(toJson(user));
    }
}
