package icom;

public class GameInfo {
	private String name_cate_game = "";
	private String game_code = "";
	private String link_game = "";
	private String user_id = "";
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
	public String getUserId()
	{
		return this.user_id;
	}
	public void SetUserId(String _userId)
	{
		this.user_id = _userId;
	}
	public GameInfo(String _nameCateGame, String _gameCode, String _linkGame, String _userId)
	{
		this.name_cate_game = _nameCateGame; this.game_code = _gameCode; this.link_game = _linkGame; this.user_id = _userId;
	}
}
