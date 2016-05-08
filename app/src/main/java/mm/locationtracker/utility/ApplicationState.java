package mm.locationtracker.utility;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class ApplicationState {

    public static String FULL_ACCESS = "fullAccess";
    public static String PARTIAL_ACCESS = "partialAccess";
    public static String LEAST_ACCESS = "leastAccess";

    private static String ACCESS_LEVEL = FULL_ACCESS;

    public static String getAccessLevel() {
        return ACCESS_LEVEL;
    }

    public static void setAccessLevel(String accessLevel) {
        ACCESS_LEVEL = accessLevel;
    }
}
