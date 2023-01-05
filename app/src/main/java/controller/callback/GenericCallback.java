package controller.callback;

public interface GenericCallback<T> {
    public static final String SUCCESS_CODE = "success";
    void callback(T msg);
}
