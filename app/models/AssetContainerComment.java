
package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class AssetContainerComment extends Model{
	
	public AssetContainerComment(Userr user, String comment) {
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

    public static Finder<Long, AssetContainerComment> find = new Finder<Long, AssetContainerComment>(Long.class, AssetContainerComment.class);
}
