package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Asset extends Model{
	
	public Asset() {
		this.creationDate = new Date();
	}

	public Asset(String title, String description, String link, Userr user) {
		this.creationDate = new Date();
		this.title = title;
		this.description = description;
		this.link = link;
		this.user = user;
	}
	
    @Id
    public Long id;
    
    public String title;
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    public String category;
    
    public String link;
    
    @Column(nullable = false)
    public Date creationDate; 
    /*
    @OneToMany(cascade=CascadeType.ALL)
    public List<Asset> activities = new ArrayList<>();
    */
    
   // @OneToMany(cascade=CascadeType.ALL)
   // public List<Tag> tags = new ArrayList<>();
    
    /*
    @OneToMany(cascade=CascadeType.ALL)
    public List<Comment> comments = new ArrayList<>();
    */
    
    @ManyToOne(cascade=CascadeType.ALL)
    public Userr user;
    
    
    public static Finder<Long, Asset> find = new Finder<Long, Asset>(Long.class, Asset.class);
    /*
     * Lista med User_
     * Lista med Asset
     */

}
