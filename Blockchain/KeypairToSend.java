package com.auxesis.auditlogger.Blockchain;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;





public class KeypairToSend {
	
	private static Web3j WEB3J = Web3j.build(new HttpService("http://139.59.213.205:7007"));
	
	// public static String main(String[] args) throws Exception {
	
     String senderPrivKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
	
			
     public String createKeypair() {
     
     byte[] reci = Hash.sha3(UUID.randomUUID().toString().getBytes());
		
			//System.out.println("value : " + reci);
			
			ECKeyPair keys = ECKeyPair.create(reci);
			
		//	System.out.println("Keys : " + keys);
			
			String address = new Address(Keys.getAddress(keys)).toString();
			
			//System.out.println("Add : "+ address);
			
			BigInteger publicKey = keys.getPublicKey();
			
			//System.out.println("PubKeys : " + publicKey);
			
			String publicKeyHex = Numeric.toHexStringWithPrefix(publicKey);
			
		   // System.out.println("public key hex : " + publicKeyHex);
		    
		    BigInteger privateKey = keys.getPrivateKey(); // public key generator
		    
		   // System.out.println("privatekey : " + privateKey );
		    
			String privateKeyHex = Numeric.toHexStringWithPrefix(privateKey);
			
			//return privateKeyHex;
			
     
			
		//	System.out.println("Private hex : " +privateKeyHex); // private key
     
     
			Credentials credentials = Credentials.create(privateKeyHex);
			
			//System.out.println("address : " +address+ "  finaladd :" +credentials.getAddress() ) ;
			
			Credentials credentialOfSender = Credentials.create(senderPrivKey);
			
			//System.out.println("sender + " + credentialOfSender);			
			
			// transfer ether to new generated account,
			
			TransactionReceipt transactionReceipt = new TransactionReceipt();
		    
			try {
				transactionReceipt = Transfer.sendFunds(WEB3J, credentialOfSender, address, BigDecimal.valueOf(3), Unit.ETHER).send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//	System.out.println("transaction reciept : " + transactionReceipt );
			
			//EthGetBalance ethGetBalance = null;
			/*try {
				ethGetBalance = WEB3J.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
				BigInteger balance = ethGetBalance.getBalance();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		
					
		   // System.out.println("Account Balance : " +balance);
			
		    return privateKeyHex;
		    
		  //  return address;
			
		
		    
		
		
	}



}
