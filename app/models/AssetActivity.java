package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class AssetActivity extends Model{
	
	@Id
	public long id;
	
    @Column(nullable = false)
    public Date creationDate;  
	
	public String message;

	public AssetActivity() {
		this.creationDate = new Date();
	}
	
	public AssetActivity(String message) {
		this.creationDate = new Date();
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public static Finder<Long, AssetActivity> find = new Finder<Long, AssetActivity>(Long.class, AssetActivity.class);
}
