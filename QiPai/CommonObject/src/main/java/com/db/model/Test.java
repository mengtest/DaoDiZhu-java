package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class Test extends DBObject {
	private static final long serialVersionUID = 5;
	public static final String tableName = "test";
	
	public final static String ID = "id";
	//id
	private Integer id = 0;
	public long getFirstId(){
		return id;
	}
	public long getSecondId(){
		return 0L;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
		sql.append("update test set").append(" where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[0];
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into test (id) values (?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[1];
		parameters[0] = this.id;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from test where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.id);
	}
	public static String toSelectSQL(Integer id){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from test where id = ").append(id);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}