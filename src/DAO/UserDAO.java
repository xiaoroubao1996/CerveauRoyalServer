package DAO;

import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO {

    public User selectByEmail(String email) {
        Connection conn = null;
        ResultSet result;
        PreparedStatement sqlPrepare;
        User user = null;
        try {
            conn = SQL.getSQLConnection();
            String sql;
            sql = "SELECT * FROM User WHERE email= ? ";
            sqlPrepare=conn.prepareStatement(sql);
            sqlPrepare.setString(1,String.valueOf(email));
            result = sqlPrepare.executeQuery();
            while (result.next()) {
                user = new User(Integer.valueOf(result.getInt("id")),
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

    public User selectByID(Integer id) {
        Connection conn = null;
        ResultSet result;
        PreparedStatement sqlPrepare;
        User user = null;
        try {
            conn = SQL.getSQLConnection();
            String sql;
            sql = "SELECT * FROM User WHERE id= (?) ";
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

    
    public ArrayList<User> selectAll() {
        ArrayList<User> resultList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement sqlPrepare;
        ResultSet result;
        try {
            conn = SQL.getSQLConnection();
            String sql;
            sql = "SELECT * FROM user";
            sqlPrepare=conn.prepareStatement(sql);
            result = sqlPrepare.executeQuery();
            while (result.next()) {
                User user = new User(Integer.valueOf(result.getInt("id")),
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

                resultList.add(user);
            }

            conn.close();
            return resultList;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    
    public void add(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement sqlPrepare;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "INSERT INTO User (nickname, avatar, email, password, deviceToken) VALUES (?,?,?,?,?)";

            sqlPrepare=conn.prepareStatement(sql);
            sqlPrepare.setString(1,user.getnickname());
            sqlPrepare.setInt(2,user.getAvatar());
            sqlPrepare.setString(3,user.getEmail());
            sqlPrepare.setString(4,user.getPassword());
            sqlPrepare.setString(5,user.getDeviceToken());
            sqlPrepare.executeUpdate();


            conn.close();
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void update(User user) {
        Connection conn = null;
        PreparedStatement sqlPrepare;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "UPDATE User SET nickname= ?, email= ?, password= ?, avatar= ?, score = ?, "
                    + "numWinLiterature = ? , numLoseLiterature = ?, numWinMath = ?, numLoseMath = ?, numWinArt = ?, numLoseArt = ?, numWinHistory = ?, numLoseHistory = ?,"
                    + "numWinMusic = ?, numLoseMusic = ?, numWinGeography = ?, numLoseGeography = ?, numWinEnglish = ?, numLoseEnglish = ?, numWinCommonsense = ?, numLoseCommonsense = ?,"
                    + "deviceToken = ?, rank = ? WHERE id = ?";

            sqlPrepare=conn.prepareStatement(sql);
            sqlPrepare.setString(1,user.getnickname());
            sqlPrepare.setString(2,user.getEmail());
            sqlPrepare.setString(3,user.getPassword());
            sqlPrepare.setInt(4,user.getAvatar());
            sqlPrepare.setInt(5,user.getScore());
            sqlPrepare.setInt(6,user.getNumWinLiterature());
            sqlPrepare.setInt(7,user.getNumLoseLiterature());
            sqlPrepare.setInt(8,user.getNumWinMath());
            sqlPrepare.setInt(9,user.getNumLoseMath());
            sqlPrepare.setInt(10,user.getNumWinArt());
            sqlPrepare.setInt(11,user.getNumLoseArt());
            sqlPrepare.setInt(12,user.getNumWinHistory());
            sqlPrepare.setInt(13,user.getNumLoseHistory());
            sqlPrepare.setInt(14,user.getNumWinMusic());
            sqlPrepare.setInt(15,user.getNumLoseMusic());
            sqlPrepare.setInt(16,user.getNumWinGeography());
            sqlPrepare.setInt(17,user.getNumLoseGeography());
            sqlPrepare.setInt(18,user.getNumWinEnglish());
            sqlPrepare.setInt(19,user.getNumWinCommonsense());
            sqlPrepare.setInt(20,user.getNumLoseCommonsense());
            sqlPrepare.setInt(21,user.getNumWinEnglish());
            sqlPrepare.setString(22,user.getDeviceToken());
            sqlPrepare.setString(23,String.valueOf(user.getRank()));
            sqlPrepare.setInt(24,user.getId());

            sqlPrepare.executeUpdate();


            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void delete(User user) {
        Connection conn = null;
        PreparedStatement sqlPrepare;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "DELETE FROM User WHERE id=?";
            sqlPrepare=conn.prepareStatement(sql);
            sqlPrepare.setInt(1,user.getId());
            sqlPrepare.executeUpdate(sql);


            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

