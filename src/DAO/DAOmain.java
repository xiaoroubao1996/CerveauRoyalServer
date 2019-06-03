package DAO;

import Model.Constant;
import Model.Question;
import Model.User;

import java.util.List;

public class DAOmain {
    public static void main(String[] args){
        UserDAO userDAO = new UserDAO();
        User user = userDAO.selectByID(1);
        System.out.println(user.getEmail());
    }
}
