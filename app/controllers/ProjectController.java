package controllers;

import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.*;
import models.Asset;
import models.AssetContainer;
import models.Project;
import models.Tag;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class ProjectController extends Controller{

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
    	
    	Logger.info("Before save");
    	project.save();
    	
    	
    	Logger.info("Backend: createProject-method.");
		return ok("Backend: createProject-method.");
	}
	
	public static Result getProjects() {
		List<Project> projects = Project.find.all();
		return ok(toJson(projects));
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
    	AssetContainer assetContainer = new AssetContainer();
    	assetContainer.title = assetContainerName;
    	assetContainer.description = assetContainerDescription;
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
	
	public static Result addAsset() {
		Logger.info("Add Asset method");
		
    	JsonNode json = request().body().asJson();
    	Long assetContainerId = json.findPath("assetContainerId").asLong();
    	String assetName = json.findPath("assetName").textValue();
    	String assetDescription = json.findPath("assetDescription").textValue();
    	
    	Asset asset = new Asset();
    	asset.title = assetName;
    	asset.description = assetDescription;
    	
    	AssetContainer assetContainer = AssetContainer.find.byId(assetContainerId);
    	assetContainer.assets.add(asset);
    	assetContainer.save();
		return ok("");
	}
}
