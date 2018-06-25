package com.cashaa.blockchain.logger.serviceInt;

import com.cashaa.blockchain.logger.model.BlockchainMappingData;

public interface IBlockchainMappinData {

	public boolean save(BlockchainMappingData blockchain_mapping_data);

	public BlockchainMappingData byemail(String email);

	public BlockchainMappingData bycontractadd(String contractAdd);
	
	public BlockchainMappingData Data();
	

}