package ru.shemplo.tasks.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.shemplo.snowball.utils.db.DBType;
import ru.shemplo.tasks.mvc.model.ListOfTasks;
import ru.shemplo.tasks.mvc.model.Task;

public class DBAccess {

    private DBConnection db;
    
    private final String 
        SQL_SELECT_ALL_LISTS     = "SELECT * FROM `lists`",
        SQL_SELECT_TASKS_OF_LIST = "SELECT * FROM `tasks` WHERE `list`=':list'",
        SQL_INSERT_TASK          = "INSERT INTO `tasks` (`list`, `desc`, `status`) "
                                 + "VALUES (':list', ':desc', '0')",
        SQL_INSERT_EXPIRE_TASK   = "INSERT INTO `tasks` (`list`, `desc`, `expire`, `status`) "
                                 + "VALUE (':list', ':desc', ':expire', '0')";
    
    private static final DateFormat 
        SQL_FORMAT = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    
    public DBAccess () {
        this.db = DBConnection.getInstanceOf (DBType.MySQL);
    }
    
    public List <ListOfTasks> getAllLists () {
        try {
            ResultSet result = db.execute (SQL_SELECT_ALL_LISTS);
            List <ListOfTasks> lists = new ArrayList <> ();
            
            while (result.next ()) {
                lists.add (ListOfTasks.valueFrom (result));
            }
            
            return lists;
        } catch (SQLException sqle) {}
        
        return new ArrayList <> ();
    }
    
    public List <Task> getTasksOfList (long listID) {
        try {
            String query = SQL_SELECT_TASKS_OF_LIST
                         . replace (":list", "" + listID);
            List <Task> tasks = new ArrayList <> ();
            ResultSet result = db.execute (query);
            
            while (result.next ()) {
                tasks.add (Task.valueFrom (result));
            }
            
            return tasks;
        } catch (SQLException sqle) {}
        
        return new ArrayList <> ();
    }
    
    public void addTask (long listID, String description, Date expire) throws SQLException {
        String query = expire == null ? SQL_INSERT_TASK : SQL_INSERT_EXPIRE_TASK;
        query = query.replace (":list", "" + listID).replace (":desc", description);
        
        if (expire != null) {
            query.replace (":expire", SQL_FORMAT.format (expire));
        }
        
        db.update (query);
    }
    
}
