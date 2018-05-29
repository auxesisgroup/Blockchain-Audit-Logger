package com.auxesis.blockchain.auditlogger.SMCservice;



import java.math.BigInteger;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;

import com.auxesis.blockchain.auditlogger.Model.Registration;
import com.auxesis.blockchain.auditlogger.Model.Transaction;

@ComponentScan

public interface SmcServiceInterface {
	
	public String DeployMappingSmartContract();
	
	//public String DeployCustomerSmartContract();
	

}
