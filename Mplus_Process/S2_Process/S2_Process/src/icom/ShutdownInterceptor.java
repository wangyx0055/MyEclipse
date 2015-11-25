package icom;

public class ShutdownInterceptor extends Thread {

    private Sender app;

    public ShutdownInterceptor(Sender app) {
        this.app = app;
    }

    @Override
	public void run() {
        System.out.println("Call the shutdown routine");
        app.windowClosing();
    }
}
