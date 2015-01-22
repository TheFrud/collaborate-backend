package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import models.Asset;
import models.AssetComment;
import models.AssetContainer;
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
public class AssetController extends Controller {
	
	public static Result addAsset() {
		Logger.info("Add Asset method");
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();
    	Long assetContainerId = json.findPath("assetContainerId").asLong();
    	String assetName = json.findPath("assetName").textValue();
    	String assetDescription = json.findPath("assetDescription").textValue();
    	String assetUrl = json.findPath("assetUrl").textValue();
    	Asset asset = new Asset(assetName, assetDescription, assetUrl, user);
    	Logger.info("Projekt id: " + projectId);
    	AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
    	assetContainer.assets.add(asset);
    	
    	
    	Project project = Project.find.byId(projectId);
    	ProjectActivity projectActivity = new ProjectActivity(user.username + " added Asset " + asset.title + " to Asset Container " + assetContainer.title + ".");
    	project.addActivity(projectActivity);
    	project.save();
		
    	
    	assetContainer.save();
    	
    	Logger.info("Asset added.");
		return ok("Asset added.");
	}
	
	public static Result getAssets() {
		List<Asset> assets = Asset.find.all();
		
		Logger.info("Assets returned to client.");
		return ok(toJson(assets));
	}
	
	public static Result getUserAssets() {
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");
		List<Asset> userAssets = Asset.find.where().eq("user", user).findList();
		
		Logger.info("Assets where user is owner returned to client.");
		return ok(toJson(userAssets));
	}
	
	public static Result getAsset() {
		JsonNode json = request().body().asJson();
		Long assetId = json.findPath("assetId").asLong();
		Asset asset = Asset.find.byId(assetId);
		
		Logger.info("Asset returned to client.");
		return ok(toJson(asset));
	}
	
	public static Result addCommentToAsset() {
		JsonNode json = request().body().asJson();
		
		Long assetId = json.findPath("assetId").asLong();
		Long projectId = json.findPath("projectId").asLong();
		Long assetContainerId = json.findPath("assetContainerId").asLong();
		String assetCommentArg = json.findPath("assetComment").textValue();
		
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");
		
		AssetComment assetComment = new AssetComment(user, assetCommentArg);
		
		Asset asset = Asset.find.byId(assetId);
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		Project project = Project.find.byId(projectId);
		
		asset.addComment(assetComment);
		
		// Sätter in aktivitet
		
		ProjectActivity projectActivity = new ProjectActivity(
				user.username + " added a comment to Asset " + asset.title + " in Asset Container " + assetContainer.title + ".");
		project.addActivity(projectActivity);	
		
		asset.save();
		project.save();
		
		Logger.info("Comment added to Asset.");
		return ok("Comment added to Asset.");
	}
	
	public static Result approveAsset() {
		JsonNode json = request().body().asJson();
		
		Long assetId = json.findPath("assetId").asLong();
		Long projectId = json.findPath("projectId").asLong();
		Long assetContainerId = json.findPath("assetContainerId").asLong();
		
		Asset asset = Asset.find.byId(assetId);
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		Project project = Project.find.byId(projectId);
		
		// Approvar asset
		asset.approve();
		
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");		
		
		// Sätter in aktivitet
		ProjectActivity projectActivity = new ProjectActivity(
				user.username + " approved Asset " + asset.title + " in Asset Container " + assetContainer.title + ".");
		project.addActivity(projectActivity);
		
		asset.save();
		project.save();
		
		Logger.info("Asset approved.");
		return ok("Asset approved.");
	}	
	
}
