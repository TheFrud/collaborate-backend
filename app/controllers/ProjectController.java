package controllers;

import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.*;
import models.Asset;
import models.AssetContainer;
import models.Project;
import models.Tag;
import models.Userr;
import actors.MyWebSocketActor;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;
import play.mvc.WebSocket;

@Security.Authenticated(Secured.class)
public class ProjectController extends Controller{
	
	
	public static WebSocket<String> socket() {
	    return WebSocket.withActor(MyWebSocketActor::props);
	}
	
	/*
	public static WebSocket<String> socket() {
	    return WebSocket.whenReady((in, out) -> {
	        out.write("Hello!");
	        out.close();
	    });
	}
	*/
	public static Result createProject() {
		
		Project project = new Project();
		
    	JsonNode json = request().body().asJson();
    	String projectTitle = json.findPath("projectTitle").textValue();
    	String projectDescription = json.findPath("projectDescription").textValue();
    	String projectSecurityPolicy = json.findPath("projectSecurityPolicy").textValue();
    	
    	JsonNode tList = json.findPath("projectTags");
    	ArrayList<Tag> projectTags = new ArrayList<>();
    	Logger.info(tList.asText());
    	Logger.info(String.valueOf(tList.size()));
    	for(JsonNode j: tList){
    		Tag tag = new Tag(j.findPath("text").asText());
    		projectTags.add(tag);
    	}
    	
    	
    	for(Tag t: projectTags){
    		Logger.info(t.text);
    	}
    	
    	project.title = projectTitle;
    	project.description = projectDescription;
    	project.securityPolicy = projectSecurityPolicy;
    	project.tags = projectTags;
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	project.owners.add(user);
    	Logger.info("Before save");
    	project.save();
    	
    	
    	Logger.info("Backend: createProject-method.");
		return ok("Backend: createProject-method.");
	}
	
	public static Result getProjects() throws InterruptedException {
		List<Project> projects = Project.find.all();
		Thread.sleep(150);
		return ok(toJson(projects));
	}
	
	public static Result getProjectsWhereUserIsOwner() {
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
		List<Project> userProjects = Project.find.where().eq("owners", user).findList();
		return ok(toJson(userProjects));
	}
	
	public static Result getProject() {
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();
		Project project = Project.find.byId(projectId);
		return ok(toJson(project));
	}
	
	public static Result addAssetContainer() {
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();	
    	Project project = Project.find.byId(projectId);
    	String assetContainerName = json.findPath("assetContainerName").textValue();
    	String assetContainerDescription = json.findPath("assetContainerDescription").textValue();
    	String projectAssetContainerCategory = json.findPath("projectAssetContainerCategory").textValue();
    	AssetContainer assetContainer = new AssetContainer();
    	assetContainer.title = assetContainerName;
    	assetContainer.description = assetContainerDescription;
    	assetContainer.category = projectAssetContainerCategory;
    	project.assetContainers.add(assetContainer);
    	project.save();
    	Logger.info("addAssetContainer-method.");
		return ok();
	}
	
	public static Result getAssetContainer() {
    	JsonNode json = request().body().asJson();
    	Long assetContainerId = json.findPath("assetContainerId").asLong();
		AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
		return ok(toJson(assetContainer));		
	}
	
	public static Result getAssetContainers() {
		List<AssetContainer> assetContainers = AssetContainer.find.all();
		return ok(toJson(assetContainers));
	}
	
	public static Result addAsset() {
		Logger.info("Add Asset method");
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	
    	JsonNode json = request().body().asJson();
    	Long assetContainerId = json.findPath("assetContainerId").asLong();
    	String assetName = json.findPath("assetName").textValue();
    	String assetDescription = json.findPath("assetDescription").textValue();
    	String assetUrl = json.findPath("assetUrl").textValue();
    	Asset asset = new Asset();
    	asset.title = assetName;
    	asset.description = assetDescription;
    	asset.link = assetUrl;
    	asset.user = user;
    	
    	AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
    	assetContainer.assets.add(asset);
    	assetContainer.save();
		return ok("");
	}
	
	public static Result getAssets() {
		List<Asset> assets = Asset.find.all();
		return ok(toJson(assets));
	}
	
	public static Result removeProject() {
		JsonNode json = request().body().asJson();
		Long projectId = json.findPath("projectId").asLong();
		Project project = Project.find.byId(projectId);
		Logger.info("Projekt togs bort.");
		project.delete();
		return ok("Project removed");
	}
	
	public static Result updateProjectDescription() {
		JsonNode json = request().body().asJson();
		Long projectId = json.findPath("projectId").asLong();
		String projectDescription = json.findPath("projectDescription").textValue();
		Project project = Project.find.byId(projectId);
		
		project.setDescription(projectDescription);
		project.save();
		Logger.info("Project updated.");
		return ok("Project updated");
	}
}
