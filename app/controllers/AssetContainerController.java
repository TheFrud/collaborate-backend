package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import models.Asset;
import models.AssetComment;
import models.AssetContainer;
import models.AssetContainerComment;
import models.Project;
import models.ProjectActivity;
import models.Userr;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

import com.fasterxml.jackson.databind.JsonNode;

@Security.Authenticated(Secured.class)
public class AssetContainerController extends Controller {
	public static Result addAssetContainer() {
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();	
    	Logger.info("Projekt id: " + projectId);
    	Project project = Project.find.byId(projectId);
    	String assetContainerName = json.findPath("assetContainerName").textValue();
    	String assetContainerDescription = json.findPath("assetContainerDescription").textValue();
    	String projectAssetContainerCategory = json.findPath("projectAssetContainerCategory").textValue();
    	AssetContainer assetContainer = new AssetContainer();
    	assetContainer.title = assetContainerName;
    	assetContainer.description = assetContainerDescription;
    	assetContainer.category = projectAssetContainerCategory;
    	
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	
    	// Sätter in aktivitet

    	ProjectActivity projectActivity = new ProjectActivity(user.username + " added Asset Container " + assetContainer.title + ".");
    	project.addActivity(projectActivity);

    	project.assetContainers.add(assetContainer);
    	project.save();
    	
    	Logger.info("Asset Container added.");
		return ok();
	}
	
	public static Result getAssetContainer() {
    	JsonNode json = request().body().asJson();
    	Long assetContainerId = json.findPath("assetContainerId").asLong();
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		
		Logger.info("Asset Container returned to client.");
		return ok(toJson(assetContainer));		
	}
	
	public static Result getAssetContainers() {
		List<AssetContainer> assetContainers = AssetContainer.find.all();
		
		Logger.info("Asset Containers returned to client.");
		return ok(toJson(assetContainers));
	}
	
	public static Result markAssetContainerAsCompleted() {
		JsonNode json = request().body().asJson();
		
		Long projectId = json.findPath("projectId").asLong();
		Long assetContainerId = json.findPath("assetContainerId").asLong();
		
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		Project project = Project.find.byId(projectId);
		
		// Approvar asset container
		assetContainer.markAsCompleted();
		
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");		
		
		// Sätter in aktivitet
		ProjectActivity projectActivity = new ProjectActivity(
				user.username + " marked Asset Container " + assetContainer.title + " as completed.");
		project.addActivity(projectActivity);
		
		assetContainer.save();
		project.save();
		
		Logger.info("Asset Container marked as completed.");
		return ok("Asset Container marked as completed.");
	}	
	
	public static Result addCommentToAssetContainer() {
		JsonNode json = request().body().asJson();
		
		Long projectId = json.findPath("projectId").asLong();
		Long assetContainerId = json.findPath("assetContainerId").asLong();
		String assetContainerCommentArg = json.findPath("assetContainerComment").textValue();
		
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");
		
		AssetContainerComment assetContainerComment = new AssetContainerComment(user, assetContainerCommentArg);
		
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		Project project = Project.find.byId(projectId);
		
		assetContainer.addComment(assetContainerComment);
		
		// Sätter in aktivitet
		
		ProjectActivity projectActivity = new ProjectActivity(
				user.username + " posted a comment to Asset Container " + assetContainer.title + ".");
		project.addActivity(projectActivity);	
		
		assetContainer.save();
		project.save();
		
		Logger.info("Comment posted to Asset Container.");
		return ok("Comment posted to Asset Container.");
	}
	
}
