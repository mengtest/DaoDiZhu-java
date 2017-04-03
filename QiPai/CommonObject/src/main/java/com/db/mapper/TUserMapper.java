package com.db.mapper;

import com.db.model.TUser;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年12月7日 上午11:52:04
 */

public interface TUserMapper {

	void insertUser(TUser user);

	void updateLoginTime(TUser user);
	TUser selectUserByUUId(String value);
	void updateUserType(TUser user);
	
}
