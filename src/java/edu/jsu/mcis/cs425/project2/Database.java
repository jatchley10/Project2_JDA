
package edu.jsu.mcis.cs425.project2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database {
    
    public Connection getConnection() throws SQLException, NamingException {
        Connection conn = null;
        try {
  
            InitialContext envContext = new InitialContext();
            Context initContext = (Context)envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
            return conn;
        }
        
        catch (SQLException | NamingException e) {}
        return conn;
    }
    
    public HashMap getUserInfo(String username) throws SQLException, NamingException{
        HashMap<String, String> info = new HashMap<String, String>();
        Database db = new Database();
        Connection conn = db.getConnection();
        String query = "SELECT * FROM cs425_p2.user where username like ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, username);
        ResultSet results = p.executeQuery();
        
        if(results.next()){
            String idStr = Integer.toString(results.getInt("id"));
            info.put("userid",idStr);
            info.put("displayname",results.getString("displayname"));
        }
        conn.close();
        return info;
    }
    
    public String getSkillsListAsHTML(int userid) throws SQLException, NamingException{
        StringBuilder sb = new StringBuilder();
        Database db = new Database();
        Connection conn = db.getConnection();
        String query = "select skills.*,a.userid \n" +
        "from cs425_p2.skills as skills\n" +
        "left join (SELECT * FROM cs425_p2.applicants_to_skills where userid = ?) as a\n" +
        "on skills.id = a.skillsid;";
        
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, Integer.toString(userid));
        ResultSet rs = p.executeQuery();
        
        while(rs.next()){
            sb.append("<input type=\"checkbox\" name=\"skills\" value=\"").append(rs.getInt("id")).append("\"");
            sb.append("id= \"skills_id_").append(rs.getString("id"));
             sb.append("\"");
            if(rs.getInt("userid") != 0){
                sb.append("checked");
            }
            sb.append("/>");
            sb.append("<label for=\"skills_id_").append(rs.getString("id")).append("\"/>").append(rs.getString("description")).append("</label></br>\n");
        }
        conn.close();
        return sb.toString();
    }
    
    public void setSkillsList(int userid, String[] skills) throws SQLException, NamingException{
        //Deleting the old data
        String query = "DELETE FROM cs425_p2.applicants_to_skills where userid = ?";
        String queryTwo = "INSERT INTO cs425_p2.applicants_to_skills (userid, skillsid)\n" +
        "VALUES (?, ?);";
        Database db = new Database();
        Connection conn = db.getConnection();
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, Integer.toString(userid));
        p.executeUpdate();
        //Insert the new data to overwrite the old
        PreparedStatement q = conn.prepareStatement(queryTwo);
        for(int i = 0 ; i < skills.length; ++i){
            q.setString(1, Integer.toString(userid));
            q.setString(2, skills[i]);
            q.execute();
        }
        conn.close();
    }
    
    public void setJobsList(int userid, String[] jobs) throws SQLException, NamingException{
        //Deleting the old data
        String query = "DELETE FROM cs425_p2.applicants_to_jobs where userid = ?";
        String queryTwo = "INSERT INTO cs425_p2.applicants_to_jobs (userid, jobsid)\n" +
                        "VALUES (?, ?);";
        Database db = new Database();
        Connection conn = db.getConnection();
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, Integer.toString(userid));
        p.executeUpdate();
        //Insert the new data to overwrite the old
        PreparedStatement q = conn.prepareStatement(queryTwo);
        for(int i = 0 ; i < jobs.length; ++i){
            q.setString(1, Integer.toString(userid));
            q.setString(2, jobs[i]);
            q.execute();
        }
        conn.close();
    }
    
    public String getJobsListAsHTML(int userid) throws SQLException, NamingException{
        Database db = new Database();
        Connection conn = db.getConnection();
        String query = "SELECT jobs.id, jobs.name, a.userid FROM\n" +
        "jobs LEFT JOIN (SELECT * FROM applicants_to_jobs WHERE userid= ?) AS a\n" +
        "ON jobs.id = a.jobsid\n" +
        "WHERE jobs.id IN\n" +
        "(SELECT jobsid AS id FROM\n" +
        "(applicants_to_skills JOIN skills_to_jobs\n" +
        "ON applicants_to_skills.skillsid = skills_to_jobs.skillsid)\n" +
        "WHERE applicants_to_skills.userid = ?)\n" +
        "ORDER BY jobs.name;";
        StringBuilder sb = new StringBuilder();
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, Integer.toString(userid));
        p.setString(2, Integer.toString(userid));
        ResultSet rs = p.executeQuery();
        
        while(rs.next()){
            sb.append("<input type=\"checkbox\" name=\"jobs\" value=\"").append(rs.getInt("id")).append("\"");
            sb.append("id= \"jobs_id_").append(rs.getString("id"));
             sb.append("\"");
            if(rs.getInt("userid")!=0){
                sb.append("checked");
            }
            sb.append("/>");
            
            sb.append("<label for=\"jobs_id_").append(rs.getString("id")).append("\"/>").append(rs.getString("name")).append("</label></br>\n");
        }
        
        return sb.toString();
    }
}
