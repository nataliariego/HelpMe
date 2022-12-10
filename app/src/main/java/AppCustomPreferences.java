import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppCustomPreferences {

    private static AppCustomPreferences instance;

    /* Indica si el usuario está conectado */
    public static final String SHARED_ONLINE = "online";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AppCustomPreferences(Activity activity) {
        this.sharedPreferences = activity.getSharedPreferences("my-preferences", Context.MODE_PRIVATE);
        editor = this.sharedPreferences.edit();
    }

    public void save(String key, Object value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public String getKey(String key) {
        return sharedPreferences.getString(key, null);
    }


    public static AppCustomPreferences getInstance(Activity activity) {
        if (instance == null) {
            instance = new AppCustomPreferences(activity);
        }
        return instance;
    }


}
