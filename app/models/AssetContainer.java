package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class AssetContainer extends Model{
	
	public AssetContainer() {
		this.creationDate = new Date();
		this.status = "needed";
	}
	
    @Id
    public Long id;
    
    public String title;
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    public String category;
    
    @Column(nullable = false)
    public Date creationDate;
    
    @OneToMany(cascade=CascadeType.ALL)
    public List<Asset> assets = new ArrayList<>();
    /*
    @OneToMany(cascade=CascadeType.ALL)
    public List<AssetContainerActivity> activities = new ArrayList<>();
    */
    // Uncompleted || Completed
    public String status;
    
    //@OneToMany(cascade=CascadeType.ALL)
    //public List<Tag> tags = new ArrayList<>();
    
    public static Finder<Long, AssetContainer> find = new Finder<Long, AssetContainer>(Long.class, AssetContainer.class);
    /*
     * Lista med User_
     * Lista med Asset
     */

}
