package my.process;

public class ShutdownInterceptor extends Thread
{

	private MainClass app;

	public ShutdownInterceptor(MainClass app)
	{
		this.app = app;
	}

	public void run()
	{
		System.out.println("Thuc thi thu tuc shutdown");
		app.Closing();
	}
}
