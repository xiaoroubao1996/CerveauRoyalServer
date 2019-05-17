package DAO;

import Model.Constant;
import Model.Question;
import Model.User;

import java.util.List;

public class DAOmain {
    public static void main(String[] args){
//        UserDAO userDAO = new UserDAO();
//        User user = new User("nickname",1, "email@email", "password", "devicetoken");
//        userDAO.add(user);
        List<Question> questionList = DAOFactory.getQuestionDAO().selectBySubject(Constant.SUBJECT.MATH);
        Question question = questionList.get(0);
        System.out.println(question.getSubject());
    }
}
