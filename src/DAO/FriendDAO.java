package DAO;

import Model.Friends;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FriendDAO {
    public static boolean user1IsFriendOfUser2(User user1, User user2){
        Connection conn = null;
        ResultSet result;
        PreparedStatement sqlPrepare;
        try {
            conn = SQL.getSQLConnection();
            String sql;
            sql = "SELECT * FROM Friend WHERE user1Id = ? AND user2Id = ?";
            sqlPrepare=conn.prepareStatement(sql);
            sqlPrepare.setInt(1,user1.getId());
            sqlPrepare.setInt(2,user2.getId());
            result = sqlPrepare.executeQuery();
            if(!result.next()){
                conn.close();
                return false;
            }else{
                conn.close();
                return true;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
