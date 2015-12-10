package secChat;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class CryptoHelper {
	
	private X509EncodedKeySpec x509KeySpec;

	public DHParameterSpec getAlgorithmParameters()throws NoSuchAlgorithmException, InvalidParameterSpecException {
		
		DHParameterSpec dhParamSpec;
		
        System.out.println
        ("Creating Diffie-Hellman parameters ...");
    AlgorithmParameterGenerator paramGen
        = AlgorithmParameterGenerator.getInstance("DH");
    paramGen.init(512);
    AlgorithmParameters params = paramGen.generateParameters();
    dhParamSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
		
		return dhParamSpec;
	}
	
	public KeyPair genKeyPair(DHParameterSpec dhParamSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		System.out.println("Getting key pair...");
		// Instantiate KeyPair Generator
		KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
		
		clientKpairGen.initialize(dhParamSpec);
		KeyPair clientKpair = clientKpairGen.generateKeyPair();
		return clientKpair;
		
	}
	
	public KeyAgreement getKeyAgreement(KeyPair keyPair) throws InvalidKeyException, NoSuchAlgorithmException
	{
		System.out.println("Getting key agreement");
        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
        clientKeyAgree.init(keyPair.getPrivate());
		return clientKeyAgree;
	}
	
	// Note Need to send this!
	public byte[] encodeKey(KeyPair keyPair)
	{
		 byte[] clientPubKeyEnc = keyPair.getPublic().getEncoded();
		 return clientPubKeyEnc;	
	}
	
	// Decode
	public PublicKey getClientPublicKey(byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
        KeyFactory serverKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec
            (publicKey);
        PublicKey serverPubKey = serverKeyFac.generatePublic(x509KeySpec);
		
        return serverPubKey;
	}
	
	public KeyPair getServerKeyPair(PublicKey publicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		DHParameterSpec dhParamSpec = ((DHPublicKey)publicKey).getParams();
        System.out.println("Server: Generate DH keypair ...");
        KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
        serverKpairGen.initialize(dhParamSpec);
        KeyPair serverKpair = serverKpairGen.generateKeyPair();
		return serverKpair;
	}
	
	public KeyAgreement getServerKeyAgreement(KeyPair keyPair) throws InvalidKeyException, NoSuchAlgorithmException
	{
        System.out.println("Server: Initialization ...");
        KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
        serverKeyAgree.init(keyPair.getPrivate());
        return serverKeyAgree;
	}
	
	public byte[] encodeServerKey(KeyPair keyPair)
	{
		byte[] serverPubKeyEnc = keyPair.getPublic().getEncoded();
		return serverPubKeyEnc;
	}
	
	////////////////////////////////////////////
	public void getServerKey(byte[] encodedKey, KeyAgreement clientKeyAgree) throws InvalidKeyException, IllegalStateException, InvalidKeySpecException, NoSuchAlgorithmException
	{
		
        KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(encodedKey);
        PublicKey serverPublicKey = clientKeyFac.generatePublic(x509KeySpec);
        System.out.println("client: Execute PHASE1 ...");
        clientKeyAgree.doPhase(serverPublicKey, true);
	}
	
	public void getClientKey(PublicKey clientKey, KeyAgreement serverKeyAgree) throws InvalidKeyException, IllegalStateException
	{
        System.out.println("server: Execute PHASE1 ...");
        serverKeyAgree.doPhase(clientKey, true);

	}
	
	public byte[] getSharedSecret(KeyAgreement keyAgreement)
	{
		byte[] sharedSecret = keyAgreement.generateSecret();
		return sharedSecret;
	}
	
	public int getSecretLength(byte[] sharedSecret)
	{
		int length = sharedSecret.length;
		return length;
	}
	
}
