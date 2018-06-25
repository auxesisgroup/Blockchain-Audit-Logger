package com.cashaa.blockchain.logger.serviceInt;


import com.cashaa.blockchain.logger.model.UserDetail;;

public interface IUserDetail {

	public boolean save(UserDetail api_token);

	public UserDetail byemail(String email);
	
	public UserDetail detail();	
	

}