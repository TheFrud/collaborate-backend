package controllers;

import java.util.List;

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
    	
    	Logger.info("Current user returned to client.");
    	return ok(toJson(user));
	}
	
    public static Result editUser() {
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	JsonNode json = request().body().asJson();
    	String bio = json.findPath("bio").textValue();

    	user.setBio(bio);
    	user.save();
    	
    	Logger.info("Current user edited.");
    	return ok("Current user edited.");
    }
    
    public static Result getUser() {
    	JsonNode json = request().body().asJson();
    	Long userId = json.findPath("userId").asLong();	
    	Userr user = Userr.find.byId(userId);
    	
    	Logger.info("User returned to client.");
    	return ok(toJson(user));
    }
    
	public static Result getUsers() {
		List<Userr> users = Userr.find.all();
		
		Logger.info("Users returned to client.");
		return ok(toJson(users));
	}
}
