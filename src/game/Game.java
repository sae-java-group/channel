package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sina.sae.memcached.SaeMemcache;


public class Game implements Serializable{
	
	private static SaeMemcache memcache = new SaeMemcache();
	
	public enum GameStatus {
		CREATED,STARTED,XWIN,XLOST,DRAW
	}
	
	public static Map<String,List<String>> winSquares =  new HashMap<String,List<String>>();
	
	static{
		winSquares.put("123", Arrays.asList("1","2","3"));
		winSquares.put("456", Arrays.asList("4","5","6"));
		winSquares.put("789", Arrays.asList("7","8","9"));
		winSquares.put("147", Arrays.asList("1","4","7"));
		winSquares.put("258", Arrays.asList("2","5","8"));
		winSquares.put("369", Arrays.asList("3","6","9"));
		winSquares.put("159", Arrays.asList("1","5","9"));
		winSquares.put("357", Arrays.asList("3","5","7"));
	}
	
	private String gameKey;
	private String userX;//用户X的id
	private String userO;//用户O的id
	private boolean xMove;//此时是否Ｘ行
	private String channelX;//用户X的channel
	private String channelO;//用户O的channel
	private GameStatus status = GameStatus.CREATED;//游戏状态,默认创建状态
	
	private List<String> xSquares = new ArrayList<String>(); 
	private List<String> oSquares = new ArrayList<String>();
	
	
	public Game(String gameKey,String userX, String userO, boolean xMove) {
		super();
		this.gameKey = gameKey;
		this.userX = userX;
		this.userO = userO;
		this.xMove = xMove;
	}

	public void xMove(String i){
		if(xMove){
			xSquares.add(i);
			xMove = false;
			checkWin();
			put();
		}
	}
	
	public void oMove(String i){
		if(!xMove){
			oSquares.add(i);
			xMove = true;
			checkWin();
			put();
		}
	}
	
	public void checkWin(){
		if(status.equals(GameStatus.STARTED)){
			for(String key:winSquares.keySet()){
				if(xSquares.containsAll(winSquares.get(key))){
					this.status = GameStatus.XWIN;
					return;
				}
				if(oSquares.containsAll(winSquares.get(key))){
					this.status = GameStatus.XLOST;
					return;
				}
				if((xSquares.size()+oSquares.size())==9){
					this.status = GameStatus.DRAW;
					return;
				}
			}
		}
	}
	public String getUserX() {
		return userX;
	}

	public void setUserX(String userX) {
		this.userX = userX;
	}

	public String getUserO() {
		return userO;
	}

	public void setUserO(String userO) {
		this.userO = userO;
	}

	public String getChannelX() {
		return channelX;
	}

	public void setChannelX(String channelX) {
		this.channelX = channelX;
	}

	public String getChannelO() {
		return channelO;
	}

	public void setChannelO(String channelO) {
		this.channelO = channelO;
	}
	

	public String getGameKey() {
		return gameKey;
	}

	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public List<String> getxSquares() {
		return xSquares;
	}

	public void setxSquares(List<String> xSquares) {
		this.xSquares = xSquares;
	}

	public List<String> getoSquares() {
		return oSquares;
	}

	public void setoSquares(List<String> oSquares) {
		this.oSquares = oSquares;
	}

	public void setxMove(boolean xMove) {
		this.xMove = xMove;
	}

	public boolean isxMove() {
		return xMove;
	}

	public void put(){
		memcache.init();
		memcache.set(gameKey, this);
	}
	
	public static Game getGameByKey(String gameKey){
		memcache.init();
		return memcache.get(gameKey);
	}

}
