package network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStatusChecker{

    public static final String TAG = "NETWORK_HANDLER";

    private ConnectivityManager connectivityManager;
    private static NetworkStatusChecker instance;

    private boolean checkConnection(Context context) {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {

                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al comprobar la conexión del dispositivo. " + e.getMessage());
        }

        return false;
    }

    public void handleConnection(Context context, ConnectionCallback callback) {
        callback.callback(checkConnection(context));
    }

    public interface ConnectionCallback {
        void callback(boolean isConnected);
    }

    public static NetworkStatusChecker getInstance() {
        if (instance == null) {
            instance = new NetworkStatusChecker();
        }
        return instance;
    }
}
