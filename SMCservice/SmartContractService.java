package com.auxesis.blockchain.auditlogger.SMCservice;

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
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;
import org.springframework.http.ResponseEntity;
import com.auxesis.blockchain.auditlogger.Model.Registration;
import com.auxesis.blockchain.auditlogger.Model.Transaction;
import com.auxesis.blockchain.auditlogger.SMCservice.SmcServiceInterface;
import com.auxesis.blockchain.auditlogger.Blockchain.KeypairToSend;
import com.auxesis.blockchain.auditlogger.smartcontract.Auditmap;
import com.auxesis.blockchain.auditlogger.smartcontract.Audit;

@Service
@Configuration
public class SmartContractService implements SmcServiceInterface {

	// private String webUrl = "http://139.59.213.205:7007"; // web url of private
	// blockchain
	private String webUrl = "http://206.189.185.246:8584"; // web url of private blockchain

	private Web3j web3 = Web3j.build(new HttpService(webUrl));

	String MappingContract = "0x74eedeca8cb56ed151ee6d3ee17eb71ee173ef82";

	BigInteger _gasLimit = BigInteger.valueOf(2099756);
	BigInteger _gasPrice = BigInteger.valueOf(1);
	private BigInteger model;

	// =============================Mapping Smart Contract function
	// calls====================================//

	public String DeployMappingSmartContract() {

		try {

			String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
			Credentials creds = Credentials.create(sendPrivateKey);
			Auditmap contract = Auditmap.deploy(web3, creds, _gasPrice, _gasLimit).send();
			return ("smartContract : " + contract.getContractAddress());
		} catch (Exception ex) {
			return ("Exception" + ex);
		}

	}

	public String DeployCustomerSmartContract(Registration regdata) {

		try {

			String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
			Credentials credsmapping = Credentials.create(sendPrivateKey);
			Auditmap contract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);

			String ethAddress = regdata.getethAddress();

			Bytes32 appName = stringToBytes32Conversion(regdata.getappName());

			String contractAddress = null;

			// System.out.println("mobile number : " +regdata.getmobileNumber());

			// BigInteger mobileNumber = BigInteger.ZERO;
			BigInteger mobileNumber = BigInteger.ZERO;

			System.out.println("mobilen  " + mobileNumber);

			if (regdata.getmobileNumber() != null) {
				System.out.println("dat1" + mobileNumber);

				mobileNumber = regdata.getmobileNumber();

				System.out.println("dat2" + mobileNumber);

				contractAddress = contract.getConAddByMobile(mobileNumber).sendAsync().get();

				System.out.println("dat3" + contractAddress);
			}

			String email = "null";

			System.out.println("email " + email);

			if (regdata.getemail() != null) {
				System.out.println("email " + email);
				email = (regdata.getemail());

				System.out.println("email " + email);

				contractAddress = contract.getConAddByEmail(stringToBytes32Conversion(email)).sendAsync().get();

				System.out.println("email " + contractAddress);

			}

			String userContractAddress = contractAddress.toString();

			System.out.println("contract Address :" + userContractAddress);

			if (!userContractAddress.equals("0x0000000000000000000000000000000000000000")) {

				return ("This User is already exist :  \n Your Contract Address : " + userContractAddress);
			}

			else {

				// System.out.println(regdata.getemail());

				BigInteger _gasLimit = BigInteger.valueOf(3916964);
				BigInteger _gasPrice = BigInteger.valueOf(1);
				Auditmap contract1 = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);
				KeypairToSend keyp = new KeypairToSend(); // Class for generated key pair and transfer ether from master
				String PrivKey = keyp.createKeypair();
				Credentials creds = Credentials.create(keyp.createKeypair());

				Audit regcontract = Audit.deploy(web3, creds, _gasPrice, _gasLimit, stringToBytes32Conversion(email),
						mobileNumber, ethAddress, appName).sendAsync().get();

				if (!contract.isValid()) {
					return ("invalid contract");
				}
				String contractadd = regcontract.getContractAddress();
				TransactionReceipt transactionReceipt = contract1
						.addDetail(contractadd, mobileNumber, stringToBytes32Conversion(email), appName).send();
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
			Auditmap mappContract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);

			String contractAd = mappContract.getConAddByEmail(stringToBytes32Conversion(email)).sendAsync().get();
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
			Auditmap mappingContract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);

			String contractAd1 = mappingContract.getConAddByMobile(mobile).sendAsync().get();
			String Addcontbymobile = contractAd1.toString();

			return ("Your Contract Address : " + Addcontbymobile);

		}

		catch (Exception ex) {
			return ("error : " + ex);
		}

	}

	public Object getAllContractDetail() {

		try {

			String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
			Credentials credsmapping = Credentials.create(sendPrivateKey);
			Auditmap mappingContract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);

			Tuple3<List<Uint256>, List<Bytes32>, List<Address>> transactionReceipt = mappingContract
					.allContractDetails().sendAsync().get();

			List<Registration> regdetail = new ArrayList<Registration>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Registration reg = new Registration();

				reg.setmobileNumber(transactionReceipt.getValue1().get(i).getValue());

				reg.setemail(new String(transactionReceipt.getValue2().get(i).getValue()).trim());

				reg.setcontractAddress(new String(transactionReceipt.getValue3().get(i).getValue()).trim());

				regdetail.add(reg);
			}

			return (Object) (regdetail);
		}

		catch (Exception ex) {
			return ("error : " + ex);
		}

	}

	public Object getDetailBycontractAdd(String contractadd) {

		try {

			System.out.println("dataa   ");
			String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
			Credentials credsmapping = Credentials.create(sendPrivateKey);
			Auditmap mappingContract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);

			Tuple2<BigInteger, byte[]> transactionReceipt = mappingContract.getDetailBycontractAdd(contractadd)
					.sendAsync().get();

			System.out.println("gfdvf   " + transactionReceipt);

			Registration regd = new Registration();

			regd.setmobileNumber(transactionReceipt.getValue1());
			regd.setemail(new String(transactionReceipt.getValue2()).trim());

			return (regd);

		}

		catch (Exception ex) {
			return ("error : " + ex);
		}

	}

	public String addappName(Registration regapp) {
		try {
			System.out.println("coming");
			String sendPrivateKey = "f989fac1c1c1b1d1e66d1e0873e129626ad18ddc329a578d25fde961c56c24bf";
			Credentials credsmapping = Credentials.create(sendPrivateKey);
			Auditmap mappingContract = Auditmap.load(MappingContract, web3, credsmapping, _gasPrice, _gasLimit);
			Bytes32 appName = stringToBytes32Conversion(regapp.getappName());
			RemoteCall<TransactionReceipt> transactionReceipt = mappingContract.addappName(appName);

			return ("value " + transactionReceipt + " Value Updated SuccessFully");
		} catch (Exception ex) {
			return ("error  " + ex);
		}

	}
	// =============================Customer Smart Contract function
	// calls====================================//

	public String addTransaction(Transaction tx) {
		try {

			System.out.println("coming");
			Credentials creds = Credentials.create(tx.getPrivateKey());
			BigInteger _gasLimit = BigInteger.valueOf(4099756);
			BigInteger _gasPrice = BigInteger.valueOf(1);
			// System.out.println(tx);

			Audit contract = Audit.load(tx.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			System.out.println("contract   " + contract.getContractAddress());

			String senderAddress = tx.getSenderAddress();
			System.out.println("1." + senderAddress);

			String recieverAddress = tx.getRecieverAddress();
			System.out.println("2." + senderAddress);

			BigInteger amount = tx.getAmount();
			System.out.println("3." + amount);

			Bytes32 cryptoName = stringToBytes32Conversion(tx.getCryptoName());
			System.out.println("4." + cryptoName);

			BigInteger timeStamp = tx.getTimeStamp();
			System.out.println("5." + timeStamp);

			BigInteger mode = tx.getMode();
			System.out.println("6." + mode);

			String txid = tx.getTxId();
			System.out.println(" 7. txid " + txid);

			List<byte[]> txId = stringToBytes32Array(txid);
			System.out.println("8. hello this is transaction id " + txId);

			TransactionReceipt transactionReceipt = contract
					.addTransaction(senderAddress, recieverAddress, txId, amount, cryptoName, timeStamp, mode).send();

			return ("value " + transactionReceipt + " Value Updated SuccessFully");

		} catch (Exception ex) {
			return ("error : " + ex);
		}
	}

	public Object TransactionDetail(Transaction tx1) {
		try {

			Credentials creds = Credentials.create(tx1.getPrivateKey());
			Audit contract = Audit.load(tx1.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionDetails().sendAsync().get();

			List<Bytes32> data3 = transactionReceipt.getValue3();
			List<Bytes32> data4 = transactionReceipt.getValue4();

			List<Transaction> txList = new ArrayList<Transaction>();
			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
				Transaction trans = new Transaction();
				System.out.println("Testing1 ...! inside function" + new String(data3.get(i).getValue()));
				System.out.println("Dek data hu ...! inside function" + new String(data4.get(i).getValue()));
				String txdata1 = new String(data3.get(i).getValue());
				String txdata2 = new String(data4.get(i).getValue());
				String datatx = txdata1 + txdata2;

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

	public Object transactionDetails2(Transaction tx2) {
		try {

			Credentials creds = Credentials.create(tx2.getPrivateKey());
			Audit contract = Audit.load(tx2.getContractAddress(), web3, creds, _gasPrice, _gasLimit);
			Tuple3<List<Bytes32>, List<Uint256>, List<Uint256>> transactionReceipt = contract.transactionDetails2()
					.sendAsync().get();

			List<Transaction> txList1 = new ArrayList<Transaction>();
			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {
				Transaction trans1 = new Transaction();
				trans1.setCryptoName(transactionReceipt.getValue1().get(i).getValue().toString());
				trans1.setTimeStamp(transactionReceipt.getValue2().get(i).getValue());
				trans1.setMode(transactionReceipt.getValue3().get(i).getValue());
				txList1.add(trans1);
			}
			return (Object) (txList1);
		} catch (Exception ex) {
			return null;
		}
	}

	public Object transactionByCrypto(String crypto, Transaction tx3) {
		try {

			Credentials creds = Credentials.create(tx3.getPrivateKey());

			Audit contract = Audit.load(tx3.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionByCrypto(crypto).sendAsync().get();

			List<Bytes32> data3 = transactionReceipt.getValue3();
			List<Bytes32> data4 = transactionReceipt.getValue4();

			List<Transaction> txListofCrypto = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transCrypto = new Transaction();

				String txdata1 = new String(data3.get(i).getValue());
				String txdata2 = new String(data4.get(i).getValue());
				String datatx = txdata1 + txdata2;

				transCrypto.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transCrypto.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
				transCrypto.setTxId(datatx);

				transCrypto.setAmount(transactionReceipt.getValue5().get(i).getValue());

				txListofCrypto.add(transCrypto);
			}
			return (Object) (txListofCrypto);
		} catch (Exception ex) {
			return null;
		}

	}

	public Object transactionByCrypto2(String crypto, Transaction tx4) {
		try {

			Credentials creds = Credentials.create(tx4.getPrivateKey());

			Audit contract = Audit.load(tx4.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

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

	public Object transactionByMode(BigInteger mode, Transaction tx5) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx5.getPrivateKey());

			Audit contract = Audit.load(tx5.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionByMode(mode).sendAsync().get();
			List<Bytes32> data3 = transactionReceipt.getValue3();
			List<Bytes32> data4 = transactionReceipt.getValue4();
			List<Transaction> txListofMode = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transMode = new Transaction();
				String txdata1 = new String(data3.get(i).getValue());
				String txdata2 = new String(data4.get(i).getValue());
				String datatx = txdata1 + txdata2;

				transMode.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transMode.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());

				transMode.setTxId(datatx);

				transMode.setAmount(transactionReceipt.getValue5().get(i).getValue());

				txListofMode.add(transMode);
			}
			return (Object) (txListofMode);

		} catch (Exception ex) {
			return null;
		}

	}

	public Object transactionByMode2(BigInteger mode, Transaction tx6) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx6.getPrivateKey());

			Audit contract = Audit.load(tx6.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract.transactionByMode2(mode)
					.sendAsync().get();

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

	public Object transactionModeByCrypto(BigInteger mode, String crypto, Transaction tx7) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx7.getPrivateKey());

			Audit contract = Audit.load(tx7.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionModeByCrypto(mode, crypto).sendAsync().get();

			List<Bytes32> data3 = transactionReceipt.getValue3();
			List<Bytes32> data4 = transactionReceipt.getValue4();

			List<Transaction> txListofMode = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transMode = new Transaction();

				transMode.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transMode.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
				String txdata1 = new String(data3.get(i).getValue());
				String txdata2 = new String(data4.get(i).getValue());
				String datatx = txdata1 + txdata2;

				transMode.setTxId(datatx);

				transMode.setAmount(transactionReceipt.getValue5().get(i).getValue());

				txListofMode.add(transMode);
			}
			return (Object) (txListofMode);

		} catch (Exception ex) {
			return null;
		}

	}

	public Object transactionModeByCrypto2(BigInteger mode, String crypto, Transaction tx8) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx8.getPrivateKey());

			Audit contract = Audit.load(tx8.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract
					.transactionModeByCrypto2(mode, crypto).sendAsync().get();

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

	public Object transactionByRecieverAddress(String recieverAdd, Transaction tx9) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx9.getPrivateKey());

			Audit contract = Audit.load(tx9.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple5<List<Address>, List<Address>, List<Bytes32>, List<Bytes32>, List<Uint256>> transactionReceipt = contract
					.transactionByRecieverAddress(recieverAdd).sendAsync().get();

			List<Bytes32> data3 = transactionReceipt.getValue3();
			List<Bytes32> data4 = transactionReceipt.getValue4();

			List<Transaction> txListofMode = new ArrayList<Transaction>();

			for (int i = 0; i < transactionReceipt.getValue1().size(); i++) {

				Transaction transMode = new Transaction();

				transMode.setSenderAddress(transactionReceipt.getValue1().get(i).getValue().toString());

				transMode.setRecieverAddress(transactionReceipt.getValue2().get(i).getValue().toString());
				String txdata1 = new String(data3.get(i).getValue());
				String txdata2 = new String(data4.get(i).getValue());
				String datatx = txdata1 + txdata2;

				transMode.setTxId(datatx);

				transMode.setAmount(transactionReceipt.getValue5().get(i).getValue());

				txListofMode.add(transMode);
			}
			return (Object) (txListofMode);

		} catch (Exception ex) {
			return null;
		}

	}

	public Object transactionByRecieverAddress2(String recieverAdd, Transaction tx10) {

		try {
			// Transaction tx3 = new Transaction();
			Credentials creds = Credentials.create(tx10.getPrivateKey());

			Audit contract = Audit.load(tx10.getContractAddress(), web3, creds, _gasPrice, _gasLimit);

			Tuple3<List<Bytes32>, List<Uint256>, List<Uint8>> transactionReceipt = contract
					.transactionByRecieverAddress2(recieverAdd).sendAsync().get();

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
	// =========================================Supporting casting codes is
	// below================================================//

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

	public Bytes32 stringToBytes32Conversion(String string) {
		byte[] byteValue = string.getBytes();
		byte[] byteValueLen32 = new byte[32];
		System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
		return new Bytes32(byteValueLen32);
	}

}
