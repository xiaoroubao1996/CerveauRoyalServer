package Model;

public class Constant {
    public enum SUBJECT {
        ART,
        COMMONSENSE,
        ENGLISH,
        GEOGRAPHY,
        HISTORY,
        LITERATURE,
        MATH,
        MUSIC
    }

    public enum RANK {
        PAWN,
        KNIGHT,
        BISHOP,
        TOWER,
        QUEEN
    }


    //String
//    public static final String JADEGATEWAY_NAME = "ControlContainer-1@cerveauroyal";
    public static final String JADEGATEWAY_NAME = "ControlContainer-1";
    public static final String ENVIRONEMENT_NAME = "environementAgent";
    public static final String SEARCH_MATCH_NAME = "searchMatchAgent";
    public static final String USER_INFO_NAME = "userInfoAgent";
    public static final String QUESTION_NAME = "questionAgent";
    public static final String MATCH_NAME = "matchAgent";
    public static final String FRIEND_NAME = "friendsAgent";


    //Integer
    public static final long MATCH_WAIT_TIME_MAX = 60000;
    public static final long MATCH_EACH_ROUND_TIME_MAX = 25000;
    public static final int SMA_GET = 1;
    public static final int SMA_POST = 2;
    public static final int SMA_PUT = 3;
    public static final int SMA_SUBSCRIBE = 4;

}
