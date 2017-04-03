package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TUser extends DBObject {
	private static final long serialVersionUID = 2;
	public static final String tableName = "t_user";
	
	public final static String USERID = "userId";
	public final static String UUID = "uuid";
	public final static String TEL = "tel";
	public final static String CREATETIME = "createTime";
	public final static String LASTLOGINTIME = "lastLoginTime";
	public final static String IP = "ip";
	public final static String TYPE = "type";
	public final static String PLATFORMID = "platformId";
	public final static String NAME = "name";
	//userId
	private Long userId = 0L;
	//用户的唯一id
	private String uuid = "";
	//手机号
	private String tel = "";
	//createTime
	private Long createTime = 0L;
	//最近登陆时间
	private Long lastLoginTime = 0L;
	//登陆的ip
	private String ip = "";
	//用户类型：0 游客，1 正常用户
	private Boolean type = false;
	//平台id
	private Integer platformId = 0;
	//name
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
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Boolean getType() {
		return type;
	}
	public void setType(Boolean type) {
		this.type = type;
	}
	public Integer getPlatformId() {
		return platformId;
	}
	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
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
	public static  byte[] getRedisKey(Long userId){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(userId);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_user set ").append("uuid = ?,").append("tel = ?,").append("createTime = ?,").append("lastLoginTime = ?,").append("ip = ?,").append("type = ?,").append("platformId = ?,").append("name = ?").append(" where ").append("userId = ").append(this.userId);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[8];
		parameters[1] = this.uuid;
		parameters[2] = this.tel;
		parameters[3] = this.createTime;
		parameters[4] = this.lastLoginTime;
		parameters[5] = this.ip;
		parameters[6] = this.type;
		parameters[7] = this.platformId;
		parameters[8] = this.name;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_user (userId,uuid,tel,createTime,lastLoginTime,ip,type,platformId,name) values (?,?,?,?,?,?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[9];
		parameters[0] = this.userId;
		parameters[1] = this.uuid;
		parameters[2] = this.tel;
		parameters[3] = this.createTime;
		parameters[4] = this.lastLoginTime;
		parameters[5] = this.ip;
		parameters[6] = this.type;
		parameters[7] = this.platformId;
		parameters[8] = this.name;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_user where ").append("userId = ").append(this.userId);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.userId);
	}
	public static String toSelectSQL(Long userId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_user where userId = ").append(userId);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}