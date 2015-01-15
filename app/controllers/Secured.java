package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.SecurityController;
import controllers.routes;
import models.User_;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator{
	
    @Override
    public String getUsername(Context ctx) {
    	Logger.info("1st level");
    	User_ user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = models.User_.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return user.getEmailAddress();
            }
        }
        return null;
    }
    

    @Override
    public Result onUnauthorized(Context ctx) {
    	Logger.info("Unauthorized");
        return unauthorized("Unauthorized");
    }

}