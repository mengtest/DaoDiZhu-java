package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TRecord extends DBObject {
	private static final long serialVersionUID = 3;
	public static final String tableName = "t_record";
	
	public final static String PLAYERID = "playerId";
	public final static String GAMETYPE = "gameType";
	public final static String WIN = "win";
	public final static String LOSE = "lose";
	//playerId
	private Long playerId = 0L;
	//gameType
	private Integer gameType = 0;
	//赢的局数
	private Integer win = 0;
	//输的局数
	private Integer lose = 0;
	public long getFirstId(){
		return 0L;
	}
	public long getSecondId(){
		return gameType;
	}
	public Long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	public Integer getGameType() {
		return gameType;
	}
	public void setGameType(Integer gameType) {
		this.gameType = gameType;
	}
	public Integer getWin() {
		return win;
	}
	public void setWin(Integer win) {
		this.win = win;
	}
	public Integer getLose() {
		return lose;
	}
	public void setLose(Integer lose) {
		this.lose = lose;
	}
	@Override
	public byte[] getRedisKey() {
		return getRedisKey(this.getPlayerId());
	}
	public static  byte[] getRedisKey(long id){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(id);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_record set ").append("win = ?,").append("lose = ?").append(" where ").append("playerId = ").append(this.playerId).append(" and ").append("gameType = ").append(this.gameType);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[2];
		parameters[0] = this.win;
		parameters[1] = this.lose;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_record (playerId,gameType,win,lose) values (?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[4];
		parameters[0] = this.playerId;
		parameters[1] = this.gameType;
		parameters[2] = this.win;
		parameters[3] = this.lose;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_record where ").append("gameType = ").append(this.gameType).append(" and ").append("playerId = ").append(this.playerId);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.playerId);
	}
	public static String toSelectSQL(Long playerId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_record where playerId = ").append(playerId);
		return sql.toString();
	}
	public static String toSelectSQL(Long playerId,Integer gameType){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_record where playerId = ").append(playerId).append(" and gameType = ").append(gameType);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}