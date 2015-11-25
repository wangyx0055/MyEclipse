package icom.gateway;

public class ShutdownInterceptor extends Thread {

    private Gateway app;

    public ShutdownInterceptor(Gateway app) {
        this.app = app;
    }

    public void run() {
        System.out.println("Call the shutdown routine");
        app.exit();
    }
}
