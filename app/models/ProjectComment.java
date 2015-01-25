package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class ProjectComment extends Model{
	
	public ProjectComment(Userr user, String comment) {
		this.creationDate = new Date();
		this.user = user;
		this.text = comment;
	}
	
    @Id
    public Long id;	
    
    @Column(columnDefinition = "TEXT")
    public String text;
    
    @ManyToOne(cascade=CascadeType.ALL)
    public Userr user;
    
    @Column(nullable = false)
    public Date creationDate; 

    public static Finder<Long, ProjectComment> find = new Finder<Long, ProjectComment>(Long.class, ProjectComment.class);
}
