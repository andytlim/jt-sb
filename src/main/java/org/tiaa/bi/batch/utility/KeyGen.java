package org.tiaa.bi.batch.utility;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class KeyGen {
	
	private static final char[] symbols;
	
	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			tmp.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			tmp.append(ch);
		symbols = tmp.toString().toCharArray();
	}

	public static String getRandomKey(int num) {
		SecureRandom sRandom = new SecureRandom();
		return new BigInteger(130, sRandom).toString(32);
	}
	
	public static String getRandomString(int length) {
		char[] buffer = new char[length];
		Random random = new Random();
		for (int idx = 0; idx < buffer.length; ++idx)
			buffer[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buffer);
	}
}