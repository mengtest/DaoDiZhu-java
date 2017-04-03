package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TRoomCreate extends DBObject {
	private static final long serialVersionUID = 4;
	public static final String tableName = "t_room_create";
	
	public final static String ID = "id";
	public final static String ROOMID = "roomId";
	public final static String PLAYERID = "playerId";
	public final static String USERID = "userId";
	public final static String CREATETIME = "createTime";
	public final static String GAMECOUNT = "gameCount";
	public final static String GAMETYPE = "gameType";
	public final static String PLAYERGAMECOUNT = "playerGameCount";
	//id
	private Long id = 0L;
	//roomId
	private Integer roomId = 0;
	//playerId
	private Long playerId = 0L;
	//userId
	private Long userId = 0L;
	//createTime
	private Long createTime = 0L;
	//gameCount
	private Integer gameCount = 0;
	//gameType
	private String gameType = "";
	//实际玩的局数
	private Integer playerGameCount = 0;
	public long getFirstId(){
		return id;
	}
	public long getSecondId(){
		return 0L;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public Long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Integer getGameCount() {
		return gameCount;
	}
	public void setGameCount(Integer gameCount) {
		this.gameCount = gameCount;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public Integer getPlayerGameCount() {
		return playerGameCount;
	}
	public void setPlayerGameCount(Integer playerGameCount) {
		this.playerGameCount = playerGameCount;
	}
	@Override
	public byte[] getRedisKey() {
		return getRedisKey(this.getId());
	}
	public static  byte[] getRedisKey(long id){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(id);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_room_create set ").append("roomId = ?,").append("playerId = ?,").append("userId = ?,").append("createTime = ?,").append("gameCount = ?,").append("gameType = ?,").append("playerGameCount = ?").append(" where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[7];
		parameters[0] = this.roomId;
		parameters[1] = this.playerId;
		parameters[2] = this.userId;
		parameters[3] = this.createTime;
		parameters[4] = this.gameCount;
		parameters[5] = this.gameType;
		parameters[6] = this.playerGameCount;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_room_create (id,roomId,playerId,userId,createTime,gameCount,gameType,playerGameCount) values (?,?,?,?,?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[8];
		parameters[0] = this.id;
		parameters[1] = this.roomId;
		parameters[2] = this.playerId;
		parameters[3] = this.userId;
		parameters[4] = this.createTime;
		parameters[5] = this.gameCount;
		parameters[6] = this.gameType;
		parameters[7] = this.playerGameCount;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_room_create where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.id);
	}
	public static String toSelectSQL(Long id){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_room_create where id = ").append(id);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}