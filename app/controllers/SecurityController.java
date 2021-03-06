package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Project;
import models.Userr;
import play.Logger;
import play.api.mvc.Session;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.Context;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.response;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

public class SecurityController extends Controller {

    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "authToken";


    public static Userr getUser() {
        return (Userr)Http.Context.current().args.get("user");
    }
    
    public static Result register() {
    	Logger.info("Client trying to register.");
    	JsonNode json = request().body().asJson();
    	String email = json.findPath("email").textValue();
    	String password = json.findPath("password").textValue();
    	String fullname = json.findPath("fullname").textValue();
    	String username = json.findPath("username").textValue();
    	
    	Userr user = new Userr(email, username, password, fullname); 
    	user.save();
    	// Send email to registred user
    	Email mail = new Email();
    	mail.setSubject("Welcome to Devjungler!");
    	mail.setFrom("Devjungler <devjungler@gmail.com>");
    	mail.addTo("TO <"+email+">");
    	// sends text, HTML or both...
    	mail.setBodyText("A text message");
    	mail.setBodyHtml(
    			"<html><body><h3>Welcome to Devjungler " + fullname + "!</h3>You are now registered. :)</body></html>");
    	MailerPlugin.send(mail);
    	
    	Logger.info("User registered. Mail sent to user's email-address.");
    	return ok("User registered. Mail sent to user's email-address.");
    }
    
    // returns an authToken	.. and user
    public static Result login() {
    	/*
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }
        Login login = loginForm.get();
        */
    	Logger.info("Client trying to login.");
    	JsonNode json = request().body().asJson();
    	String email = json.findPath("email").textValue();
    	String password = json.findPath("password").textValue();
		
    	Userr user = null;
    	
    	try{
		user = Userr.findByEmailAddressAndPassword(email, password);
    	}catch(Exception e){
    	}

        if (user == null) {
        	Logger.info("Found no user in database.");
            return unauthorized();
        }
        else {
        	Logger.info("Found user!");
            String authToken = user.createToken();
            ObjectNode authTokenJson = Json.newObject();
            JsonNode userJson = Json.toJson(user);
            authTokenJson.put(AUTH_TOKEN, authToken);
            authTokenJson.put("USER", userJson);
            // session(AUTH_TOKEN, authToken);
            // response().setCookie(AUTH_TOKEN, authToken, -1, "/", "127.0.0.1", false, false);
            Logger.info("User got token");
            return ok(authTokenJson);
        }
    	}
        
    // @Security.Authenticated(Secured.class)
    public static Result logout() {

		// response().discardCookie(AUTH_TOKEN);
		try {
			getUser().deleteAuthToken();
		} catch (Exception e) {
			Logger.info("User is already logged out: " + e.getMessage());
		}

		Logger.info("User logged out");
		return ok("User logged out");
	}

    @Security.Authenticated(Secured.class)
    public static Result isUserAdminOfProject() {
        
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();
    	
		Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	
    	Project project = Project.find.byId(projectId);
    	
    	Logger.info("Checked if user was admin of project.");
    	
    	for(int i = 0; i < project.owners.size(); i++) {
    		
    		if(user.equals(project.owners.get(i))){
    			return ok("User is admin");
    		}
    		
    	}
    	return unauthorized("User was not admin");
        
        
        
    }    
    
    
    public static class Login {

        @Constraints.Required
        @Constraints.Email
        public String emailAddress;

        @Constraints.Required
        public String password;

    }


}
