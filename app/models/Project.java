package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Project extends Model{
	
	public Project() {
		this.creationDate = new Date();
	}
	
    @Id
    public Long id;
    
    @ManyToMany(cascade=CascadeType.ALL)
    public List<Userr> owners = new ArrayList<>();
    
    @Column(length = 256, nullable = false)
    @Constraints.Required
    @Constraints.MinLength(1)
    @Constraints.MaxLength(256)
    public String title;
    
    @Column(nullable = false)
    public Date creationDate;   
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    public void setDescription(String description) {
    	this.description = description;
    }
    
    public String securityPolicy;
    
    @OneToMany(cascade=CascadeType.ALL)
    public List<Tag> tags = new ArrayList<>();
    
    @OneToMany(cascade=CascadeType.ALL)
    public List<AssetContainer> assetContainers = new ArrayList<>();
    
    // public ArrayList<Comment> comments = new ArrayList<>();
    
    // public ArrayList<Contributor> contributors = new ArrayList<>();
    
    // public ArrayList<Owner> members = new ArrayList<>();
    
    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);
}
