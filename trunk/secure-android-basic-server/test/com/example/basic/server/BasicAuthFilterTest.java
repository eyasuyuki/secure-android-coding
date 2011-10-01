package com.example.basic.server;

import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;

public class BasicAuthFilterTest extends TestCase {
	private static final String TEST = "TEST";
	private static final String USER = "user0001";
	private static final String PASSWORD = "test0001";
	
	public void testDigest() throws NoSuchAlgorithmException {
        String byteData1 = DigestUtils.sha256Hex(TEST);
        //System.out.println(byteData1);
        
        String byteData2 = DigestUtils.sha256Hex(TEST);
        //System.out.println(byteData2);
        
        assertEquals(byteData1, byteData2);
	}
	public void testGetSalt() {
		byte[] salt = BasicAuthFilter.getSalt(TEST);
		assertNotNull(salt);
		assertNotSame(TEST, salt);
		//System.out.println(new String(salt));
	}
	
	public void testGetPasswordHash() throws DecoderException {
		byte[] hash1 = BasicAuthFilter.getPasswordHash(USER, PASSWORD);
		assertNotNull(hash1);
		String hex1 = Hex.encodeHexString(hash1);
		System.out.println(hex1);
		byte[] byte1 = Hex.decodeHex(hex1.toCharArray());
		assertNotNull(byte1);
		assertTrue(ArrayUtils.isEquals(hash1, byte1));
		
		byte[] hash2 =BasicAuthFilter.getPasswordHash(USER, PASSWORD);
		assertNotNull(hash2);
		System.out.println(Hex.encodeHexString(hash2));
		assertTrue(ArrayUtils.isEquals(hash1, hash2));
	}
}
