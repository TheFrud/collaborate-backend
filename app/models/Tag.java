package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Tag extends Model{

	public Tag(String text) {
		this.text = text;
	}
	
	public Tag() {
		
	}
	
    @Id
    public Long id;
    
    public String text;
    
    public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class, Tag.class);
    
    public void setText(String text){this.text = text;};
}
