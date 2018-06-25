package com.cashaa.blockchain.logger.serviceInt;

import com.cashaa.blockchain.logger.model.BlockchainServiceRegistry;

public interface IBlockchainServiceRegistry {

	public boolean save(BlockchainServiceRegistry blockchain_service_registry);

	public BlockchainServiceRegistry byappname(String appName);

	public BlockchainServiceRegistry bycontractadd(String contractAdd);
	
	public BlockchainServiceRegistry registry();

	

}