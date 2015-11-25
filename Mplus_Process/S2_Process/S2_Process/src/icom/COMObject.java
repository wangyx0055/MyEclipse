package icom;

/*********
 * Object nay duoc define dung de luu cac thong tin can save vao db
 * 
 * *********/
public class COMObject {
	private String mlist="";
	private String last_code="";
	private String user_id="";
	private String command_code="";
	
	/***************
	 * Cac truong dung de luu thong tin game, sau nay dung vao viec doi soat
	 * user_id, title (the loai game), Ma Game, Link
	 */
	private String name_cate_game = "";
	private String game_code = "";
	private String link_game = "";
	
	public String getMlist()
	{
		return this.mlist;
	}
	public void SetMlist(String _mlist)
	{
		this.mlist = _mlist;
	}
	public String getLastCode()
	{
		return this.last_code;
	}
	public void SetLastCode(String _lastCode)
	{
		this.last_code = _lastCode;
	}
	public String getUserId()
	{
		return this.user_id;
	}
	public void SetUserId(String _userId)
	{
		this.user_id = _userId;
	}
	public String getCommandCode()
	{
		return this.command_code;
	}
	public void SetCommandCode(String _commandCode)
	{
		this.command_code = _commandCode;
	}
	public String getNameCateGame()
	{
		return this.name_cate_game;
	}
	public void SetNameCateGame(String _nameCateGame)
	{
		this.name_cate_game = _nameCateGame;
	}
	public String getGameCode()
	{
		return this.game_code;
	}
	public void SetGameCode(String _gameCode)
	{
		this.game_code = _gameCode;
	}
	public String getLinkGame()
	{
		return this.link_game;
	}
	public void SetLinkGame(String _linkGame)
	{
		this.link_game = _linkGame;
	}
	/*********
	 * Object dung vao viec luu thong tin last code vao table mlist voi dk user id
	 * *****/
	public COMObject(String _mlist, String _lastCode, String _userId)
	{
		this.mlist =_mlist; this.last_code = _lastCode; this.user_id = _userId;
	}
	/*********
	 * Object dung vao viec luu thong tin last code vao table list send voi dk user id va command code
	 * *****/
	public COMObject(String _mlist, String _lastCode, String _userId,String _commandCode)
	{
		this.mlist =_mlist; this.last_code = _lastCode; this.user_id = _userId;this.command_code = _commandCode;
	}
	/*********
	 * Object dung vao viec luu thong tin game vao db de sau nay tinh doi soat
	 * *****/
	public COMObject(GameInfo game)
	{
		this.name_cate_game = game.getNameCateGame(); this.game_code = game.getGameCode(); 
		this.link_game = game.getLinkGame();this.user_id = game.getUserId();
	}
}
