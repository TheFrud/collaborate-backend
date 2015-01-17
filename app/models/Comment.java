package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Comment extends Model{
	
	public Comment() {
		this.creationDate = new Date();
	}
	
    @Id
    public Long id;	
    
    @Column(columnDefinition = "TEXT")
    public String text;
    
    public Userr user;
    
    @Column(nullable = false)
    public Date creationDate; 

    public static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
}
