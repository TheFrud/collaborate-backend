package controllers;

import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.*;
import models.Asset;
import models.AssetComment;
import models.AssetContainer;
import models.AssetContainerActivity;
import models.Project;
import models.ProjectActivity;
import models.Tag;
import models.Userr;
import actors.MyWebSocketActor;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
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

    	// Sätter in aktivitet

    	ProjectActivity projectActivity = new ProjectActivity(user.username + " created project.");
    	project.activities.add(projectActivity);

    	project.save();
    	
    	
    	Logger.info("Backend: createProject-method.");
		return ok("Backend: createProject-method.");
	}
	
	public static Result getProjects() {
		List<Project> projects = Project.find.all();
		return ok(toJson(projects));
	}
	
	public static Result getProjectsWhereUserIsOwner() {
		Logger.info("1");
		Context ctx = Context.current();
		Logger.info("2");
    	Userr user =  (Userr) ctx.args.get("user");
    	Logger.info("3");
		List<Project> userProjects = Project.find.where().eq("owners", user).findList();
		Logger.info("4");
		Logger.info(userProjects.get(0).title);
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
    	
		return ok("");
	}
	
	public static Result getAssets() {
		List<Asset> assets = Asset.find.all();
		return ok(toJson(assets));
	}
	
	public static Result getUserAssets() {
		Context ctx = Context.current();
		Userr user = (Userr) ctx.args.get("user");
		List<Asset> userAssets = Asset.find.where().eq("user", user).findList();
		return ok(toJson(userAssets));
	}
	
	public static Result getAsset() {
		JsonNode json = request().body().asJson();
		Long assetId = json.findPath("assetId").asLong();
		Asset asset = Asset.find.byId(assetId);
		return ok(toJson(asset));
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
		
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
		
		// Sätter in aktivitet

		ProjectActivity projectActivity = new ProjectActivity(user.username + " updated Project Description.");
		project.addActivity(projectActivity);

		project.save();
		Logger.info("Project updated.");
		return ok("Project updated");
	}
	
	public static Result getUsers() {
		List<Userr> users = Userr.find.all();
		return ok(toJson(users));
	}
	
	public static Result addOwnerToProject() {
		JsonNode json = request().body().asJson();
		String userEmail = json.findPath("userEmail").textValue();
		Long projectId = json.findPath("projectId").asLong();
		Userr addedUser = Userr.find.where().eq("emailAddress", userEmail).findUnique();
		Project project = Project.find.byId(projectId);
		project.addOwner(addedUser);
		
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
		
		// Sätter in aktivitet
		
		ProjectActivity projectActivity = new ProjectActivity(user.username + " added " + addedUser.username + " as owner to Project.");
		project.addActivity(projectActivity);		
		
		// Mailar medlemmen som blev tillagd.
		
    	Email mail = new Email();
    	mail.setSubject("Devjungler: Added to project!");
    	mail.setFrom("Devjungler <thefrud@email.com>");
    	mail.addTo("TO <"+userEmail+">");
    	// sends text, HTML or both...
    	mail.setBodyText("A text message");
    	mail.setBodyHtml(
    			"<html><body><b>" + addedUser.fullName + "</b>, you have been added as an owner to project <b>" + project.title + "</b>. </body></html>");
    	MailerPlugin.send(mail);		
		
		
		project.save();
		return ok("That went well");
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
		
		return ok("Comment added");
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
		
		return ok("Asset approved.");
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
		
		return ok("Asset Container marked as completed");
	}

}
