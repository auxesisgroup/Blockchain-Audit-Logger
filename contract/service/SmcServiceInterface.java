package com.auxesis.auditlogger.contract.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;

import com.auxesis.auditlogger.model.Registration;
import com.auxesis.auditlogger.model.Transaction;

@ComponentScan
public interface SmcServiceInterface {

	public String deploysmartcontractforglobalcontract();

	public String deploysmartcontract(Registration regdata);

	public Object getConAddByEmail(String email);

	public Object getConAddByMobile(BigInteger mobile);

	public Object addTransaction(Transaction tx);

	public Object transactionDetails(Transaction Tx1);

	public Object transactionDetails2(Transaction Tx2);
	
    public Object transactionByMode1(BigInteger mode, Transaction tx3);
    
    public Object transactionByMode2(BigInteger mode, Transaction tx4);
    
    public Object transactionByCrypto(String crypto, Transaction tx5);
    
    public Object transactionByCrypto2(String crypto, Transaction tx6);

}