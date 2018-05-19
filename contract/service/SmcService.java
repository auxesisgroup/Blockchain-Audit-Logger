package com.auxesis.auditlogger.contract.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.springframework.http.ResponseEntity;

import com.auxesis.auditlogger.model.Registration;
import com.auxesis.auditlogger.model.RegistrationMapContract;
import com.auxesis.auditlogger.model.Transaction;
import com.auxesis.auditlogger.smartcontract.Audit;
import com.auxesis.auditlogger.smartcontract.Auditmap;
import com.auxesis.auditlogger.Blockchain.KeypairToSend;

@Service
@Configuration

public class SmcService implements SmcServiceInterface {

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
				Audit regcontract = Audit.deploy(web3, creds, _gasPrice, _gasLimit, email, mobileNumber, ethAddress, appName).sendAsync()
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
			
			Audit contract = Audit.load(tx.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			System.out.println("contract   " + contract.getContractAddress());

			String senderAddress = tx.getSenderAddress();
			String recieverAddress = tx.getRecieverAddress();
			Bytes32 txId = stringToBytes32Conversion(tx.getTxId());
			BigInteger amount = tx.getAmount();
			Bytes32 cryptoName = stringToBytes32Conversion(tx.getCryptoName());
			BigInteger timeStamp = tx.getTimeStamp();
			BigInteger mode = tx.getMode();
			TransactionReceipt transactionReceipt = contract.addTransaction(senderAddress, recieverAddress, txId, amount, cryptoName, timeStamp, mode).send();
			return ("value " + transactionReceipt + " Value Updated SuccessFully");
		} catch (Exception ex) {
			return ("error : " + ex);
		}
	}

	public Object transactionDetails(Transaction tx1) {
		try {

			Credentials creds = Credentials.create(tx1.getPrivateKey());
			Audit contract = Audit.load(tx1.getContractAddress(), web3, creds, _gasPrice, _gasLimit);
			Tuple4<List<Address>, List<Address>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionDetails().sendAsync().get();
			List<Transaction> txList = new ArrayList<Transaction>();
			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
				Transaction trans = new Transaction();
				trans.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());
				
				trans.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
				
				trans.setTxId(new String(transactionReceipt.getValue3().get(i).getValue()).trim());
				
				trans.setAmount(transactionReceipt.getValue4().get(i).getValue());
				
				txList.add(trans);
			}
			return (Object) (txList);
		} catch (Exception ex) {
			return null;
		}
	}

	public Object transactionDetails2(Transaction tx2) {
		try {
			Credentials creds = Credentials.create(tx2.getPrivateKey());

			Audit contract = Audit.load(tx2.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract.transactionDetails2()
					.sendAsync().get();

			List<Transaction> txList1 = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction trans1 = new Transaction();

				trans1.setCryptoName(new String(transactionReceipt.getValue1().get(i).getValue()).trim());

				trans1.setTimeStamp(transactionReceipt.getValue2().get(i).getValue());

				trans1.setMode(transactionReceipt.getValue3().get(i).getValue());

				txList1.add(trans1);
			}

			return (Object) (txList1);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public Object transactionByMode(BigInteger mode, Transaction tx3 ) {

		try {
			//Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx3.getPrivateKey());

			Audit contract = Audit.load(tx3.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple4<List<Address>, List<Address>, List<Bytes32>, List<Uint256>> transactionReceipt = contract.transactionByMode(mode).sendAsync().get();

			List<Transaction> txListofMode = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transMode = new Transaction();

				transMode.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transMode.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());

				transMode.setTxId(new String(transactionReceipt.getValue3().get(i).getValue()).trim());

				transMode.setAmount(transactionReceipt.getValue4().get(i).getValue());

				txListofMode.add(transMode);
			}
			return (Object) (txListofMode);

		} catch (Exception ex) {
			return null;
		}

	}
	
	public Object transactionByMode2(BigInteger mode, Transaction tx4 ) {

		try {
			//Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx4.getPrivateKey());

			Audit contract = Audit.load(tx4.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract.transactionByMode2(mode).sendAsync().get();

			List<Transaction> txListofMode1 = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transMode1 = new Transaction();

				 transMode1.setCryptoName(new String(transactionReceipt.getValue1().get(i).getValue()).trim());
				 transMode1.setTimeStamp(transactionReceipt.getValue2().get(i).getValue());
			     transMode1.setMode(transactionReceipt.getValue3().get(i).getValue());             
			     

			     txListofMode1.add(transMode1);
			}
			return (Object) (txListofMode1);

		} catch (Exception ex) {
			return null;
		}

	}
	
	
	public Object transactionByCrypto(String crypto, Transaction tx5) {
		try {

		
			Credentials creds = Credentials.create(tx5.getPrivateKey());

			Audit contract = Audit.load(tx5.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple4<List<Address>, List<Address>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionByCrypto(crypto).sendAsync().get();

			List<Transaction> txListofCrypto = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transCrypto = new Transaction();

				transCrypto.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transCrypto.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());

				transCrypto.setTxId(new String(transactionReceipt.getValue3().get(i).getValue()).trim());

				transCrypto.setAmount(transactionReceipt.getValue4().get(i).getValue());
				
				txListofCrypto.add(transCrypto);
			}
			return (Object) (txListofCrypto);
			} catch (Exception ex) {
				return null;
			}

	}
	
	public Object transactionByCrypto2(String crypto, Transaction tx6) {
		try {

			Credentials creds = Credentials.create(tx6.getPrivateKey());

			Audit contract = Audit.load(tx6.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract.transactionByCrypto2(crypto)
					.sendAsync().get();

			List<Transaction> txListofCrypto1 = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transCrypto1 = new Transaction();

				transCrypto1.setCryptoName(new String(transactionReceipt.getValue1().get(i).getValue()).trim());

				transCrypto1.setTimeStamp(transactionReceipt.getValue2().get(i).getValue());

				transCrypto1.setMode(transactionReceipt.getValue3().get(i).getValue());

				txListofCrypto1.add(transCrypto1);
			}
			return (Object) (txListofCrypto1);
		} catch (Exception ex) {
			return null;
		}

	}

//	@RequestMapping(value = "/TransactionByMode2/{mode}", method =
//	 RequestMethod.GET, headers = {
//	 "Accept=application/json" })
//	 public ResponseEntity<Object> transactionByMode2(@PathVariable("mode")
//	 BigInteger mode) {
//	 try {
//	
//	 Transaction tx4 = new Transaction();
//	 Credentials creds = Credentials.create(tx4.getprivateKey());
//	
//	 Audit contract = Audit.load(txContractAddress, web3, creds, _gasPrice,
//	 _gasLimit);
//	
//	 Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt =
//	 contract.transactionByMode2(mode)
//	 .sendAsync().get();
//	
//	 List<Transaction> txListofMode1 = new ArrayList<Transaction>();
//	
//	 for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
//	
//	 Transaction transMode1 = new Transaction();
//	
//	 transMode1.setcryptoName(new
//	 String(transactionReceipt.getValue1().get(i).getValue()).trim());
//	
//	 transMode1.settimeStamp(transactionReceipt.getValue2().get(i).getValue());
//	
//	 transMode1.setmode(transactionReceipt.getValue3().get(i).getValue());
//	
//	 txListofMode1.add(transMode1);
//	 }
//	
//	 return new ResponseEntity<Object>(txListofMode1, HttpStatus.OK);
//	
//	 }
//	 /*"senderAddress":"0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
//	 "recieverAddress":"0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
//	 "txId":"0x692a70d2e424a56d2c6c27aa97d1a86395877b3b",
//	 "amount": 45,
//	 "cryptoName" : "btc",
//	 "timeStamp": 145799,
//	 "mode":0,
//	 "contractAddress":"0x2d0f7e779cf842259ce79694f9452a8b33d617c7"*/
//	 }catch(
//
//	Exception ex)
//	{
//		return new ResponseEntity<Object>(ex, HttpStatus.OK);
//	}
//	}


	//
	// @RequestMapping(value = "/transactionByCrypto2/{crypto}", method =
	// RequestMethod.GET, headers = {
	// "Accept=application/json" })
	//
	// public ResponseEntity<Object> transactionByCrypto2(@PathVariable("crypto")
	// String crypto) {
	// try {
	//
	// Transaction tx6 = new Transaction();
	// Credentials creds = Credentials.create(tx6.getprivateKey());
	//
	// Audit contract = Audit.load(txContractAddress, web3, creds, _gasPrice,
	// _gasLimit);
	//
	// Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt =
	// contract.transactionByCrypto2(crypto)
	// .sendAsync().get();
	//
	// List<Transaction> txListofCrypto1 = new ArrayList<Transaction>();
	//
	// for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
	//
	// Transaction transCrypto1 = new Transaction();
	//
	// transCrypto1.setcryptoName(new
	// String(transactionReceipt.getValue1().get(i).getValue()).trim());
	//
	// transCrypto1.settimeStamp(transactionReceipt.getValue2().get(i).getValue());
	//
	// transCrypto1.setmode(transactionReceipt.getValue3().get(i).getValue());
	//
	// txListofCrypto1.add(transCrypto1);
	// }
	// return new ResponseEntity<Object>(txListofCrypto1, HttpStatus.OK);
	// } catch (Exception ex) {
	// return new ResponseEntity<Object>(ex, HttpStatus.OK);
	// }
	// }
	//
	// @RequestMapping(value = "/transactionByRecieverAddress/{recieverAdd}", method
	// = RequestMethod.GET, headers = {
	// "Accept=application/json" })
	// public ResponseEntity<Object>
	// transactionByRecieverAddress(@PathVariable("recieverAdd") Address
	// recieverAdd) {
	// try {
	//
	// Transaction tx7 = new Transaction();
	// Credentials creds = Credentials.create(tx7.getprivateKey());
	//
	// Audit contract = Audit.load(txContractAddress, web3, creds, _gasPrice,
	// _gasLimit);
	//
	// Tuple4<List<Address>, List<Address>, List<Bytes32>, List<Uint256>>
	// transactionReceipt = contract
	// .transactionByRecieverAddress(recieverAdd).sendAsync().get();
	//
	// List<Transaction> txListofRecieverAdd = new ArrayList<Transaction>();
	//
	// for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
	//
	// Transaction transRecieverAdd = new Transaction();
	//
	// transRecieverAdd.setsenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());
	//
	// transRecieverAdd.setrecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
	//
	// transRecieverAdd.settxId(new
	// String(transactionReceipt.getValue3().get(i).getValue()).trim());
	//
	// transRecieverAdd.setamount(transactionReceipt.getValue4().get(i).getValue());
	//
	// txListofRecieverAdd.add(transRecieverAdd);
	// }
	// return new ResponseEntity<Object>(txListofRecieverAdd, HttpStatus.OK);
	// } catch (Exception ex) {
	// return new ResponseEntity<Object>(ex, HttpStatus.OK);
	// }
	// }
	//
	// @RequestMapping(value = "/transactionByRecieverAddress2/{recieverAdd}",
	// method = RequestMethod.GET, headers = {
	// "Accept=application/json" })
	// public ResponseEntity<Object>
	// transactionByRecieverAddress2(@PathVariable("recieverAdd") Address
	// recieverAdd) {
	// try {
	// Transaction tx8 = new Transaction();
	// Credentials creds = Credentials.create(tx8.getprivateKey());
	// Audit contract = Audit.load(txContractAddress, web3, creds, _gasPrice,
	// _gasLimit);
	//
	// Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt =
	// contract
	// .transactionByRecieverAddress2(recieverAdd).sendAsync().get();
	//
	// List<Transaction> txListofRecieverAdd1 = new ArrayList<Transaction>();
	//
	// for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
	//
	// Transaction transRecieverAdd1 = new Transaction();
	//
	// transRecieverAdd1.setcryptoName(new
	// String(transactionReceipt.getValue1().get(i).getValue()).trim());
	//
	// transRecieverAdd1.settimeStamp(transactionReceipt.getValue2().get(i).getValue());
	//
	// transRecieverAdd1.setmode(transactionReceipt.getValue3().get(i).getValue());
	//
	// txListofRecieverAdd1.add(transRecieverAdd1);
	// }
	// return new ResponseEntity<Object>(txListofRecieverAdd1, HttpStatus.OK);
	// } catch (Exception ex) {
	// return new ResponseEntity<Object>(ex, HttpStatus.OK);
	// }
	// }
	//
	// @RequestMapping(value = "/transactionModeByCrypto/{mode}/{crypto}", method =
	// RequestMethod.GET)
	//
	// public ResponseEntity<Object> transactionModeByCrypto(@PathVariable("mode")
	// BigInteger mode,
	// @PathVariable("crypto") String crypto) {
	// try {
	// Transaction tx9 = new Transaction();
	// Credentials creds = Credentials.create(tx9.getprivateKey());
	// Audit contract = Audit.load(txContractAddress, web3, creds, _gasPrice,
	// _gasLimit);
	//
	// Tuple4<List<Address>, List<Address>, List<Bytes32>, List<Uint256>>
	// transactionReceipt = contract
	// .transactionModeByCrypto(mode, crypto).sendAsync().get();
	//
	// List<Transaction> txListofModeByCrypto = new ArrayList<Transaction>();
	//
	// for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
	//
	// Transaction transModeByCrypto = new Transaction();
	//
	// transModeByCrypto.setsenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());
	//
	// transModeByCrypto.setrecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
	//
	// transModeByCrypto.settxId(new
	// String(transactionReceipt.getValue3().get(i).getValue()).trim());
	//
	// transModeByCrypto.setamount(transactionReceipt.getValue4().get(i).getValue());
	//
	// txListofModeByCrypto.add(transModeByCrypto);
	// }
	// return new ResponseEntity<Object>(txListofModeByCrypto, HttpStatus.OK);
	// } catch (Exception ex) {
	// return new ResponseEntity<Object>(ex, HttpStatus.OK);
	// }
	// }

	// Function for casting of string to bytes32.

	public Bytes32 stringToBytes32Conversion(String string) {
		byte[] byteValue = string.getBytes();
		byte[] byteValueLen32 = new byte[32];
		System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
		return new Bytes32(byteValueLen32);
	}

	@Override
	public Object transactionByMode1(BigInteger mode, Transaction tx3) {
		// TODO Auto-generated method stub
		return null;
	}

}