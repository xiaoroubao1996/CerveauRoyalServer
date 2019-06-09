package DAO;

public class DAOFactory {
    public static UserDAO  getUserDAO(){
        return new UserDAO();
    }
    public static QuestionDAO  getQuestionDAO(){
        return new QuestionDAO();
    }

    public static FriendDAO getFriendDAO(){
        return new FriendDAO();
    }
}
