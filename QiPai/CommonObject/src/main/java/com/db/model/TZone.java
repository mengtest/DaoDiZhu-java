package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TZone extends DBObject {
	private static final long serialVersionUID = 3;
	public static final String tableName = "t_zone";
	
	public final static String ZONEID = "zoneId";
	public final static String NAME = "name";
	//zoneId
	private Integer zoneId = 0;
	//name
	private String name = "";
	private int type ;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getFirstId(){
		return zoneId;
	}
	public long getSecondId(){
		return 0L;
	}
	public Integer getZoneId() {
		return zoneId;
	}
	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public byte[] getRedisKey() {
		return getRedisKey(this.getZoneId());
	}
	public static  byte[] getRedisKey(Integer zoneId){
		StringBuilder key = new StringBuilder();
		key.append(tableName).append(StringUtil.COLON).append(zoneId);
		return key.toString().getBytes();
	}
	@Override
	public String toUpdateSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("update t_zone set ").append("name = ?").append(" where ").append("zoneId = ").append(this.zoneId);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[1];
		parameters[1] = this.name;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_zone (zoneId,name) values (?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[2];
		parameters[0] = this.zoneId;
		parameters[1] = this.name;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_zone where ").append("zoneId = ").append(this.zoneId);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.zoneId);
	}
	public static String toSelectSQL(Integer zoneId){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_zone where zoneId = ").append(zoneId);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}