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


    public ArrayList<Friends> selectByUserID(Integer userId) {
        ArrayList<Friends> resultList=new ArrayList<>();
        Connection conn = null;
        ResultSet result;
        PreparedStatement sqlPrepare;
        try {
            conn = SQL.getSQLConnection();
            String sql;
            sql = "SELECT * FROM Friend WHERE id=  ";
            sqlPrepare=conn.prepareStatement(sql);

            sqlPrepare.setInt(1,id);
            result = sqlPrepare.executeQuery();
            while (result.next()) {
                user = new User(result.getInt("id"),
                        result.getString("email"),
                        result.getString("nickname"),
                        result.getInt("avatar"),
                        result.getString("password"),
                        result.getInt("score"),
                        result.getInt("numWinLiterature"),
                        result.getInt("numLoseLiterature"),
                        result.getInt("numWinMath"),
                        result.getInt("numLoseMath"),
                        result.getInt("numWinArt"),
                        result.getInt("numLoseArt"),
                        result.getInt("numWinHistory"),
                        result.getInt("numLoseHistory"),
                        result.getInt("numWinMusic"),
                        result.getInt("numLoseMusic"),
                        result.getInt("numWinGeography"),
                        result.getInt("numLoseGeography"),
                        result.getInt("numWinEnglish"),
                        result.getInt("numLoseEnglish"),
                        result.getInt("numWinCommonsense"),
                        result.getInt("numLoseCommonsense"),
                        result.getString("deviceToken"),
                        result.getString("rank")
                );
            }
            conn.close();
            return user;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
