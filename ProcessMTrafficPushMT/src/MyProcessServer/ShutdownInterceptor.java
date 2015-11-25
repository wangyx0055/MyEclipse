package MyProcessServer;

public class ShutdownInterceptor extends Thread
{

	private MainProcess app;

	public ShutdownInterceptor(MainProcess app)
	{
		this.app = app;
	}

	public void run()
	{
		System.out.println("Call the shutdown routine");
		app.WindowClosing();
	}
}
