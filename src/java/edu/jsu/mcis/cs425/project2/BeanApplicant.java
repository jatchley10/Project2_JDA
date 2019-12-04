package edu.jsu.mcis.cs425.project2;

import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.NamingException;

public class BeanApplicant {
    
    private String username;
    int userid;
    private String displayname;
    private String[] skills;
    private String[] jobs;

    public int getUserid(){
        return this.userid;
    }
    
    public String getUsername() {
        return username;
    }
    public String getDisplayname(){
        return this.displayname;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setSkills(String[] skills){
        this.skills = skills;
    }
    
    public void setJobs(String[] jobs){
        this.jobs = jobs;
    }
    
    public void setUserInfo() throws SQLException, NamingException {
        Database db = new Database();
        HashMap<String, String> userinfo = db.getUserInfo(username);
        userid = Integer.parseInt(userinfo.get("userid"));
        displayname = userinfo.get("displayname");
    }
    
    public String getSkillsList() throws SQLException, NamingException {
        Database db = new Database();
        return ( db.getSkillsListAsHTML(userid) );
    }
    
    public void setSkillsList() throws SQLException, NamingException {
        Database db = new Database();
        db.setSkillsList(userid, skills);
    }
    public void setJobsList() throws SQLException, NamingException {
        Database db = new Database();
        db.setJobsList(userid, jobs);
    }
    
    public String[] getSkills(){
        return this.skills;
    }
    public String[] getJobs(){
        return this.jobs;
    }
 
    public String getJobsList() throws SQLException, NamingException {
        Database db = new Database();
        return ( db.getJobsListAsHTML(userid) );
    }
}