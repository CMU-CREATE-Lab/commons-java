package edu.cmu.ri.createlab.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * Performs one-way encryption using the SHA-1 algorithm.
 * </p>
 * <p>
 * Based on code from: http://www.devbistro.com/articles/Java/Password-Encryption
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EncryptionService
   {
   private static final EncryptionService INSTANCE = new EncryptionService();

   public static EncryptionService getInstance()
      {
      return INSTANCE;
      }

   /**
    * One-way encrypts the given {@link String} using the SHA-1 algorithm and returns a Base-64 encoded representation
    * of the encryption.  Returns <code>null</code> if the given string is <code>null</code>.
    */
   public String encrypt(final String plaintext)
      {
      if (plaintext != null)
         {
         final MessageDigest messageDigest;
         try
            {
            messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(plaintext.getBytes());
            }
         catch (NoSuchAlgorithmException e)
            {
            throw new IllegalStateException("Encryption failed due to a NoSuchAlgorithmException exception", e);
            }

         return new String(Base64Coder.encode(messageDigest.digest()));
         }

      return null;
      }

   private EncryptionService()
      {
      // private to prevent instantiation
      }
   }
