/*
 * Copyright (C) 2013-2016 by XDEV Software, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.xdev.security.authentication.db;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public interface DBHashStrategy
{
	// TODO customize salt strength?
	public byte[] hashPassword(final byte[] password);



	public class MD5 implements DBHashStrategy
	{

		@Override
		public byte[] hashPassword(final byte[] password)
		{
			try
			{
				final MessageDigest md5Digest = MessageDigest.getInstance("MD5");
				return md5Digest.digest(password);
			}
			catch(final NoSuchAlgorithmException e)
			{
				throw new RuntimeException(e);
			}
		}

	}



	public class SHA2 implements DBHashStrategy
	{

		@Override
		public byte[] hashPassword(final byte[] password)
		{
			try
			{
				final MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
				return sha256Digest.digest(password);
			}
			catch(final NoSuchAlgorithmException e)
			{
				throw new RuntimeException(e);
			}
		}
	}



	public class SHA1 implements DBHashStrategy
	{

		@Override
		public byte[] hashPassword(final byte[] password)
		{
			try
			{
				final MessageDigest sha256Digest = MessageDigest.getInstance("SHA-1");
				return sha256Digest.digest(password);
			}
			catch(final NoSuchAlgorithmException e)
			{
				throw new RuntimeException(e);
			}
		}
	}



	// TODO random key storage within db
	public class PBKDF2WithHmacSHA1 implements DBHashStrategy
	{
		@Override
		public byte[] hashPassword(final byte[] password)
		{
			final byte[] salt = new byte[16];
			byte[] hash = null;
			for(int i = 0; i < 16; i++)
			{
				salt[i] = (byte)i;
			}
			try
			{
				final KeySpec spec = new PBEKeySpec(new String(password).toCharArray(),salt,65536,
						128);
				final SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				hash = f.generateSecret(spec).getEncoded();

			}
			catch(final NoSuchAlgorithmException nsale)
			{
				nsale.printStackTrace();

			}
			catch(final InvalidKeySpecException ikse)
			{
				ikse.printStackTrace();
			}
			return hash;
		}
	}
}
