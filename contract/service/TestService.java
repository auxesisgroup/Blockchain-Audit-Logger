package com.auxesis.auditlogger.contract.service;


	import java.io.IOException;
	import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
	import java.util.concurrent.ExecutionException;

	import org.jboss.logging.Param;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.stereotype.Service;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.CrossOrigin;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestMethod;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.ResponseBody;
	import org.springframework.web.bind.annotation.RestController;
	import org.web3j.abi.datatypes.Address;
	import org.web3j.abi.datatypes.generated.Bytes32;
	import org.web3j.abi.datatypes.generated.Uint256;
	import org.web3j.abi.datatypes.generated.Uint8;
	import org.web3j.crypto.Credentials;
	import org.web3j.protocol.Web3j;
	import org.web3j.protocol.core.RemoteCall;
	import org.web3j.protocol.core.methods.response.TransactionReceipt;
	import org.web3j.protocol.core.methods.response.Web3ClientVersion;
	import org.web3j.protocol.http.HttpService;
	import org.web3j.tuples.generated.Tuple3;
	import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;
    import org.springframework.http.ResponseEntity;

	import com.auxesis.auditlogger.model.Registration;
	import com.auxesis.auditlogger.model.RegistrationMapContract;
	import com.auxesis.auditlogger.model.Transaction;

import com.auxesis.auditlogger.smartcontract.AuditTestbytes32;
	import com.auxesis.auditlogger.smartcontract.Auditmap;
	import com.auxesis.auditlogger.Blockchain.KeypairToSend;

	@Service
	@Configuration
	public class TestService {

		private String webUrl = "http://139.59.213.205:7007";

		String contractAddressOfMapping = "0x9000ec36f490c5468b80e85d4178f7bd47ba4a46"; // just for example we will deploy
																						// this every

		private Web3j web3 = Web3j.build(new HttpService(webUrl));

		Registration regContract = new Registration();
		String regcontractAddress = regContract.contractAddress();
		Transaction txContract = new Transaction();
		Transaction txprivateKey = new Transaction();
		String txPrivateKey = txprivateKey.getPrivateKey();
		String txContractAddress = txContract.getContractAddress();

		BigInteger _gasLimit = BigInteger.valueOf(2099756);
		BigInteger _gasPrice = BigInteger.valueOf(1);

		public String deploysmartcontractforglobalcontract() {

			try {

				String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
				Credentials creds = Credentials.create(sendPrivateKey);
				Auditmap contract = Auditmap.deploy(web3, creds, _gasPrice, _gasLimit).send();
				return ("smartContract : " + contract.getContractAddress());
			} catch (Exception ex) {
				return ("Exception" + ex);
			}

		}

		public String deploysmartcontract(Registration regdata) {

			try {

				String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
				Credentials credsmapping = Credentials.create(sendPrivateKey);
				Auditmap contract = Auditmap.load(contractAddressOfMapping, web3, credsmapping, _gasPrice, _gasLimit);

				Bytes32 email = stringToBytes32Conversion(regdata.getemail());

				BigInteger mobileNumber = regdata.getmobileNumber();

				String ethAddress = regdata.getethAddress();

				Bytes32 appName = stringToBytes32Conversion(regdata.getappName());

				String contractAddress = contract.getConAddByMobile(mobileNumber).sendAsync().get();

				// System.out.println( "contract Address :" + contractAddress.toString());
				String userContractAddress = contractAddress.toString();

				System.out.println("contract Address :" + userContractAddress);

				if (!userContractAddress.equals("0x0000000000000000000000000000000000000000")) {

					return ("This User is already exist :  \n Your Contract Address : " + userContractAddress);
				}

				else {

					// System.out.println(regdata.getemail());

					BigInteger _gasLimit = BigInteger.valueOf(4099756);
					BigInteger _gasPrice = BigInteger.valueOf(1);
					Auditmap contract1 = Auditmap.load(contractAddressOfMapping, web3, credsmapping, _gasPrice, _gasLimit);
					KeypairToSend keyp = new KeypairToSend(); // Class for generated key pair and transfer ether from master
					String PrivKey = keyp.createKeypair();
					Credentials creds = Credentials.create(keyp.createKeypair());
					AuditTestbytes32 regcontract = AuditTestbytes32.deploy(web3, creds, _gasPrice, _gasLimit, email, mobileNumber, ethAddress, appName).sendAsync()
							.get();

					if (!contract.isValid()) {
						return ("invalid contract");
					}
					String contractadd = regcontract.getContractAddress();
					TransactionReceipt transactionReceipt = contract1.addDetail(contractadd, mobileNumber, email).send();
					
					return ("Your Private Key : " + PrivKey + " , Your Contract Address : " + contractadd);

				}
			}

			catch (Exception ex) {

				return ("Exception : " + ex);
			}
		}

		public String getConAddByEmail(String email) {

			try {
				String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
				Credentials credsmapping = Credentials.create(sendPrivateKey);
				Auditmap mappContract = Auditmap.load(contractAddressOfMapping, web3, credsmapping, _gasPrice, _gasLimit);

				String contractAd = mappContract.getConAddByEmail(email).sendAsync().get();
				String Addcontbyemail = contractAd.toString();

				return ("Your Contract Address " + Addcontbyemail);
			}

			catch (Exception ex) {
				return ("error : " + ex);
			}

		}

		public String getConAddByMobile(BigInteger mobile) {

			try {

				String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
				Credentials credsmapping = Credentials.create(sendPrivateKey);
				Auditmap mappingContract = Auditmap.load(contractAddressOfMapping, web3, credsmapping, _gasPrice,
						_gasLimit);

				String contractAd1 = mappingContract.getConAddByMobile(mobile).sendAsync().get();
				String Addcontbymobile = contractAd1.toString();

				return ("Your Contract Address : " + Addcontbymobile);

			}

			catch (Exception ex) {
				return ("error : " + ex);
			}

		}

		public String addTransaction(Transaction tx) {
			try {
				
				System.out.println("coming");
				Credentials creds = Credentials.create(tx.getPrivateKey());
				BigInteger _gasLimit = BigInteger.valueOf(4099756);
				BigInteger _gasPrice = BigInteger.valueOf(1);
				//System.out.println(tx);
				
				AuditTestbytes32 contract = AuditTestbytes32.load(tx.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

				System.out.println("contract   " + contract.getContractAddress());

				String senderAddress = tx.getSenderAddress();
				System.out.println("1." +senderAddress );
				
				String recieverAddress = tx.getRecieverAddress();
				System.out.println("2." +senderAddress );
				
				BigInteger amount = tx.getAmount();
				System.out.println("3." +amount );				
				
			    Bytes32 cryptoName = stringToBytes32Conversion(tx.getCryptoName());
			    System.out.println("4." +cryptoName );
			    
			    
				BigInteger timeStamp = tx.getTimeStamp();
				System.out.println("5." +timeStamp );
				
				BigInteger mode = tx.getMode();			
				System.out.println("6." +mode );
				
				String txid = tx.getTxId(); 
				System.out.println(" 7. txid " +txid);
				
            	List<byte[]> txId = stringToBytes32Array(txid);
                System.out.println("8. hello this is transaction id " +txId);           
              
                TransactionReceipt transactionReceipt = contract.addTransaction(senderAddress, recieverAddress,  txId, amount, cryptoName, timeStamp, mode).send();
				
			    return ("value " + transactionReceipt + " Value Updated SuccessFully");
			   
			} catch (Exception ex) {
				return ("error : " + ex);
			}
		}
		


		public Object transactionDetails(Transaction tx1) {
			try {

				Credentials creds = Credentials.create(tx1.getPrivateKey());
				AuditTestbytes32 contract = AuditTestbytes32.load(tx1.getContractAddress(), web3, creds, _gasPrice, _gasLimit);
				Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract.transactionDetails().sendAsync().get();
				
				System.out.println("Dekho mai data hu ...! ");
			
				List<Bytes32> data3 = transactionReceipt.getValue3();
				List<Bytes32> data4 = transactionReceipt.getValue4();	
				System.out.println("Dekho mai data hu ...! yaha v" +data3+ "................"+data4);
			    List<Transaction> txList = new ArrayList<Transaction>();
				for (int i = 0; i < transactionReceipt.getValue1().size(); i++)
				 {
					Transaction trans = new Transaction();
					System.out.println("Testing1 ...! inside function"+new String(data3.get(i).getValue()));
					System.out.println("Dek data hu ...! inside function"+new String(data4.get(i).getValue()));
					String txdata1 = new String(data3.get(i).getValue());
					String txdata2 = new String(data4.get(i).getValue());					
					String datatx = txdata1+txdata2;			
					
                    trans.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());					
					trans.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());			
				    trans.setTxId(datatx);					
					trans.setAmount(transactionReceipt.getValue5().get(i).getValue());
                		
					
					txList.add(trans);
				}
				return (Object) (txList);
			} catch (Exception ex) {
				return null;
			}
		}



//
//		public Object transactionDetails2(Transaction tx2) {
//			try {
//				Credentials creds = Credentials.create(tx2.getPrivateKey());
//
//				Audit contract = Audit.load(tx2.getContractAddress(), web3, creds, _gasPrice, _gasLimit);
//
//				Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract.transactionDetails2()
//						.sendAsync().get();
//
//				List<Transaction> txList1 = new ArrayList<Transaction>();
//
//				for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
//
//					Transaction trans1 = new Transaction();
//
//					trans1.setCryptoName(new String(transactionReceipt.getValue1().get(i).getValue()).trim());
//
//					trans1.setTimeStamp(transactionReceipt.getValue2().get(i).getValue());
//
//					trans1.setMode(transactionReceipt.getValue3().get(i).getValue());
//
//					txList1.add(trans1);
//				}
//
//				return (Object) (txList1);
//			} catch (Exception ex) {
//				return null;
//			}
//		}
//		
//	

		// Function for casting of string to bytes32.

		
		public static List<byte[]> stringToBytes32Array(String s) {
			// s = "12bdc4917747e6c9f4195a431285cd224a4dd093c3213404d6f5809bc8d8dfb0";
			System.out.println("stringToBytes32Array..............." + s);
			List<byte[]> arrayBytes = new ArrayList<byte[]>();
			int interval = 32;
			
			int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
			
			String[] result = new String[arrayLength];
			
			int j = 0;
			int lastIndex = result.length - 1;

			System.out.println("result..............." + result.length);

			for (int i = 0; i < lastIndex; i++) {
				result[i] = s.substring(j, j + interval);
				j += interval;
			}
			// Add the last bit 
			result[lastIndex] = s.substring(j);
			for (int p = 0; p < result.length; p++) {
				System.out.println("result............... 2 : " + result.length);
				System.out.println("asciiValue..............." + p + "  " + result[p]);
				byte[] bytes = Numeric.hexStringToByteArray(asciiToHex(result[p]));
				arrayBytes.add(bytes);
			}

			return arrayBytes;
		}

		public static String asciiToHex(String asciiValue) {
			char[] chars = asciiValue.toCharArray();
			StringBuffer hex = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				hex.append(Integer.toHexString((int) chars[i]));
			}
			return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length() / 2), "00"));
		}
		
																																																																																					// =
		public Bytes32 stringToBytes32Conversion(String string) {
			byte[] byteValue = string.getBytes();
			byte[] byteValueLen32 = new byte[32];
			System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
			return new Bytes32(byteValueLen32);
		}	
		
		

	}
