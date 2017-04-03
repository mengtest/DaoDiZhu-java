package com.db.model;
import com.db.base.DBObject;
import com.common.utils.StringUtil;
public class TPay extends DBObject {
	private static final long serialVersionUID = 1;
	public static final String tableName = "t_pay";
	
	public final static String ID = "id";
	public final static String USERID = "userId";
	public final static String DIAMOND = "diamond";
	public final static String CREATETIME = "createTime";
	//id
	private Integer id = 0;
	//userId
	private Long userId = 0L;
	//diamond
	private Integer diamond = 0;
	//createTime
	private Long createTime = 0L;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getDiamond() {
		return diamond;
	}
	public void setDiamond(Integer diamond) {
		this.diamond = diamond;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
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
		sql.append("update t_pay set ").append("userId = ?,").append("diamond = ?,").append("createTime = ?").append(" where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public Object[] toUpdateSQLParameters(){
		Object[] parameters = new Object[3];
		parameters[0] = this.userId;
		parameters[1] = this.diamond;
		parameters[2] = this.createTime;
		return parameters;
	}
	@Override
	public String toInsertSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_pay (id,userId,diamond,createTime) values (?,?,?,?)");
		return sql.toString();
	}
	@Override
	public Object[] toInsertSQLParameters(){
		Object[] parameters = new Object[4];
		parameters[0] = this.id;
		parameters[1] = this.userId;
		parameters[2] = this.diamond;
		parameters[3] = this.createTime;
		return parameters;
	}
	@Override
	public String toDeleteSQL() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from t_pay where ").append("id = ").append(this.id);
		return sql.toString();
	}
	@Override
	public String toSelectSQL() {
		return toSelectSQL(this.id);
	}
	public static String toSelectSQL(Integer id){
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_pay where id = ").append(id);
		return sql.toString();
	}
	public static String toSelectAllSQL(){
		return "select * from " + tableName;
	}
}