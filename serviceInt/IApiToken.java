package com.cashaa.blockchain.logger.serviceInt;

import com.cashaa.blockchain.logger.model.ApiToken;

public interface IApiToken {

	public boolean save(ApiToken api_token);

	public ApiToken byname(String name);

	public ApiToken byapikey(String apikey);
	public ApiToken token();
	
	

}