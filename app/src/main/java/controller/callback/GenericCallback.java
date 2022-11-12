package controller.callback;

public interface GenericCallback {
    public static final String SUCCESS_CODE = "success";
    void callback(String msg);
}
