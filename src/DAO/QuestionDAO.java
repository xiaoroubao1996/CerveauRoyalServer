package DAO;

import Model.Constant;
import Model.Question;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionDAO{
    
    public Question selectByID(Integer id) {
        Connection conn = null;
        PreparedStatement sqlPrepare;
        ResultSet result;
        Question question=null;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "select * from Question where id=?";
            sqlPrepare = conn.prepareStatement(sql);
            sqlPrepare.setInt(1, id);
            result = sqlPrepare.executeQuery();
            while (result.next()) {
                question = new Question(result.getInt("id"),
                        result.getString("subject"),
                        result.getString("text"),
                        result.getString("option1"),
                        result.getString("option2"),
                        result.getString("option3"),
                        result.getString("option4"),
                        result.getInt("answer")
                        );
            }

            conn.close();
            return question;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public ArrayList<Question> selectAll() {
        ArrayList<Question> resultList=new ArrayList<>();
        Connection conn = null;
        PreparedStatement sqlPrepare;
        ResultSet result;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "select * from Question";
            sqlPrepare = conn.prepareStatement(sql);
            result = sqlPrepare.executeQuery();
            while (result.next()) {
                Question question = new Question(result.getInt("id"),
                        result.getString("subject"),
                        result.getString("text"),
                        result.getString("option1"),
                        result.getString("option2"),
                        result.getString("option3"),
                        result.getString("option4"),
                        result.getInt("answer")
                );
                resultList.add(question);
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



    public ArrayList<Question> selectBySubject(String subject) {
        ArrayList<Question> resultList=new ArrayList<>();
        Connection conn = null;
        PreparedStatement sqlPrepare;
        ResultSet result;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "select * from Question WHERE subject=?";
            sqlPrepare = conn.prepareStatement(sql);
            sqlPrepare.setString(1, subject);
            result=sqlPrepare.executeQuery();
            while (result.next()) {
                Question question = new Question(result.getInt("id"),
                        result.getString("subject"),
                        result.getString("text"),
                        result.getString("option1"),
                        result.getString("option2"),
                        result.getString("option3"),
                        result.getString("option4"),
                        result.getInt("answer")
                );
                resultList.add(question);
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

    public ArrayList<Question> selectBySubject(Constant.SUBJECT subject) {
        ArrayList<Question> resultList=new ArrayList<>();
        Connection conn = null;
        PreparedStatement sqlPrepare;
        ResultSet result;
        try {
            conn = SQL.getSQLConnection();

            String sql;
            sql = "select * from Question WHERE subject=?";
            sqlPrepare = conn.prepareStatement(sql);
            sqlPrepare.setString(1, subject.toString());
            result=sqlPrepare.executeQuery();
            while (result.next()) {
                Question question = new Question(result.getInt("id"),
                        result.getString("subject"),
                        result.getString("text"),
                        result.getString("option1"),
                        result.getString("option2"),
                        result.getString("option3"),
                        result.getString("option4"),
                        result.getInt("answer")
                );
                resultList.add(question);
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
}
