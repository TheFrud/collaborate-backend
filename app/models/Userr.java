package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Userr extends Model {

    @Id
    public Long id;

    private String authToken;
    
    // Ens assets här borde inte tas bort ifall projektet tas bort?
    // Så att man får credden.
    /*
    @OneToMany(cascade=CascadeType.ALL)
    public List<Asset> myAssets = new ArrayList<>();  
    */
    
    public String username;
    
    @Column(columnDefinition = "TEXT")
    public String bio;
    
    public void setBio(String bio) {
    	this.bio = bio;
    }
    
    public long rating;
    
    @Column(length = 256, unique = true, nullable = false)
    @Constraints.MaxLength(256)
    @Constraints.Required
    @Constraints.Email
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
    }

    @Column(length = 64, nullable = false)
    private byte[] shaPassword;

    @Transient
    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    @JsonIgnore
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        shaPassword = getSha512(password);
    }

    @Column(length = 256, nullable = false)
    @Constraints.Required
    @Constraints.MinLength(2)
    @Constraints.MaxLength(256)
    public String fullName;

    @Column(nullable = false)
    public Date creationDate;

    public String createToken() {
        authToken = UUID.randomUUID().toString();
        save();
        return authToken;
    }

    public void deleteAuthToken() {
        authToken = null;
        save();
    }
    
    public Userr() {
        this.creationDate = new Date();
    }

    public Userr(String emailAddress, String username, String password, String fullName) {
        setEmailAddress(emailAddress);
        setPassword(password);
        this.username = username;
        this.fullName = fullName;
        this.creationDate = new Date();
    }


    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Finder<Long, Userr> find = new Finder<Long, Userr>(Long.class, Userr.class);
    
    public static Userr findByAuthToken(String authToken) {
    	System.out.println("--- 1");
        if (authToken == null) {
            return null;
        }

        try  {
        	System.out.println("--- 2");
            return find.where().eq("authToken", authToken).findUnique();
        }
        catch (Exception e) {
        	System.out.println("--- 3 --- Nullskit");
            return null;
        }
    }

    public static Userr findByEmailAddressAndPassword(String emailAddress, String password) {
        // todo: verify this query is correct.  Does it need an "and" statement?
    	System.out.println("I metoden");
        return find.where().eq("emailAddress", emailAddress.toLowerCase()).eq("shaPassword", getSha512(password)).findUnique();
    }

}