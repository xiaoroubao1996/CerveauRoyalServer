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


    //String
    public static final String JADEGATEWAY_NAME = "ControlContainer-1@cerveauroyal";
    public static final String ENVIRONEMENT_NAME = "environementAgent";
    public static final String SEARCH_MATCH_NAME = "searchMatchAgent";
    public static final String USER_INFO_NAME = "userInfoAgent";



    //Integer
    public static final long MATCH_WAIT_TIME_MAX = 30;
    public static final int SMA_GET = 1;
    public static final int SMA_POST = 2;
    public static final int SMA_PUT = 3;

}
