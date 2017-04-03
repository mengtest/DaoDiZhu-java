package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TPlayer extends DBObject {
	private static final long serialVersionUID = 2;
	public static final String tableName = "t_player";
	
	public final static String USERID = "userId";
	public final static String PLAYERID = "playerId";
	public final static String CREATETIME = "createTime";
	public final static String DIAMOND = "diamond";
	public final static String NAME = "name";
	//userId
	private Long userId = 0L;
	//playerId
	private Long playerId = 0L;
	//createTime
	private Long createTime = 0L;
	//钻石
	private Integer diamond = 0;
	//玩家名字
	private String name = "";
	public long getFirstId(){
		return userId;
	}
	public long getSecondId(){
		return 0L;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Integer getDiamond() {
		return diamond;
	}
	public void setDiamond(Integer diamond) {
		this.diamond = diamond;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public byte[] getRedisKey() {
		return getRedisKey(this.getUserId());
	}
	public static  byte[] getRedisKey(long id){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(id);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_player set ").append("playerId = ?,").append("createTime = ?,").append("diamond = ?,").append("name = ?").append(" where ").append("userId = ").append(this.userId);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[4];
		parameters[0] = this.playerId;
		parameters[1] = this.createTime;
		parameters[2] = this.diamond;
		parameters[3] = this.name;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_player (userId,playerId,createTime,diamond,name) values (?,?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[5];
		parameters[0] = this.userId;
		parameters[1] = this.playerId;
		parameters[2] = this.createTime;
		parameters[3] = this.diamond;
		parameters[4] = this.name;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_player where ").append("userId = ").append(this.userId);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.userId);
	}
	public static String toSelectSQL(Long userId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_player where userId = ").append(userId);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}