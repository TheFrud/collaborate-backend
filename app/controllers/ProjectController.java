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

    	for(JsonNode j: tList){
    		Tag tag = new Tag(j.findPath("text").asText());
    		projectTags.add(tag);
    	}
    	
    	project.title = projectTitle;
    	project.description = projectDescription;
    	project.securityPolicy = projectSecurityPolicy;
    	project.tags = projectTags;
    	
    	Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
    	
    	project.owners.add(user);

    	// Sätter in aktivitet
    	ProjectActivity projectActivity = new ProjectActivity(user.username + " created project.");
    	project.activities.add(projectActivity);

    	project.save();
    	
    	Logger.info("Project created.");
		return ok("Project created.");
	}
	
	public static Result getProjects() {
		List<Project> projects = Project.find.all();
		Logger.info("Projects returned to client.");
		return ok(toJson(projects));
	}
	
	public static Result getProjectsWhereUserIsOwner() {
		Context ctx = Context.current();
    	Userr user =  (Userr) ctx.args.get("user");
		List<Project> userProjects = Project.find.where().eq("owners", user).findList();
		
		Logger.info("Projects where user is owner returned to client.");
		return ok(toJson(userProjects));
	}
	
	public static Result getProject() {
    	JsonNode json = request().body().asJson();
    	Long projectId = json.findPath("projectId").asLong();
		Project project = Project.find.byId(projectId);
		
		Logger.info("Project returned to client.");
		return ok(toJson(project));
	}
	

	
	public static Result removeProject() {
		JsonNode json = request().body().asJson();
		Long projectId = json.findPath("projectId").asLong();
		Project project = Project.find.byId(projectId);
		project.delete();
		
		Logger.info("Project removed.");
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
		
		Logger.info("Project description updated.");
		return ok("Project description updated");
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
		
		Logger.info("A new owner added to project. New owner got notified by email.");
		return ok("A new owner added to project. New owner got notified by email.");
	}

}
