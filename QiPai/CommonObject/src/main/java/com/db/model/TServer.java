package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TServer extends DBObject {
	private static final long serialVersionUID = 1;
	public static final String tableName = "t_server";
	
	public final static String ZONEID = "zoneId";
	public final static String SERVERID = "serverId";
	public final static String IP = "ip";
	public final static String PORT = "port";
	//zoneId
	private Integer zoneId = 0;
	//serverId
	private Integer serverId = 0;
	//ip
	private String ip = "";
	//port
	private Integer port = 0;
	
	private Integer status;
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public long getFirstId(){
		return 0L;
	}
	public long getSecondId(){
		return serverId;
	}
	public Integer getZoneId() {
		return zoneId;
	}
	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	@Override
	public byte[] getRedisKey() {
		return getRedisKey(this.getZoneId(),this.getServerId());
	}
	public static  byte[] getRedisKey(Integer zoneId,Integer serverId){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(serverId).append(StringUtil.COLON).append(zoneId);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_server set ").append("ip = ?,").append("port = ?").append(" where ").append("zoneId = ").append(this.zoneId).append(" and ").append("serverId = ").append(this.serverId);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[2];
		parameters[2] = this.ip;
		parameters[3] = this.port;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_server (zoneId,serverId,ip,port) values (?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[4];
		parameters[0] = this.zoneId;
		parameters[1] = this.serverId;
		parameters[2] = this.ip;
		parameters[3] = this.port;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_server where ").append("serverId = ").append(this.serverId).append(" and ").append("zoneId = ").append(this.zoneId);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.zoneId);
	}
	public static String toSelectSQL(Integer zoneId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_server where zoneId = ").append(zoneId);
		return sql.toString();
	}
	public static String toSelectSQL(Integer zoneId,Integer serverId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_server where zoneId = ").append(zoneId).append(" and serverId = ").append(serverId);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}