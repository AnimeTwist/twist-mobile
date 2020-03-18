package dev.smoketrees.twist.utils

import dev.smoketrees.twist.BuildConfig
import java.nio.charset.StandardCharsets
import java.security.DigestException
import java.security.InvalidAlgorithmParameterException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {
    fun generateKeyAndIV(
        keyLength: Int,
        ivLength: Int,
        iterations: Int,
        salt: ByteArray?,
        password: ByteArray,
        md: MessageDigest
    ): Array<ByteArray?> {

        val digestLength = md.digestLength
        val requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength
        val generatedData = ByteArray(requiredLength)
        var generatedLength = 0

        try {
            md.reset()

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength)
                md.update(password)
                if (salt != null)
                    md.update(salt, 0, 8)
                md.digest(generatedData, generatedLength, digestLength)

                // additional rounds
                for (i in 1 until iterations) {
                    md.update(generatedData, generatedLength, digestLength)
                    md.digest(generatedData, generatedLength, digestLength)
                }

                generatedLength += digestLength
            }

            // Copy key and IV into separate byte arrays
            val result = arrayOfNulls<ByteArray>(2)
            result[0] = generatedData.copyOfRange(0, keyLength)
            if (ivLength > 0)
                result[1] = generatedData.copyOfRange(keyLength, keyLength + ivLength)

            return result

        } catch (e: DigestException) {
            throw RuntimeException(e)

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, 0.toByte())
        }
    }

    fun decryptSourceUrl(sourceUrl: String): String {
        val cipherData = android.util.Base64.decode(sourceUrl, android.util.Base64.DEFAULT)
        val saltData = Arrays.copyOfRange(cipherData, 8, 16)

        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        val keyAndIV =
            generateKeyAndIV(32, 16, 1, saltData, BuildConfig.CRYPTO_KEY, md5!!)
        val key = SecretKeySpec(keyAndIV[0], "AES")
        val iv = IvParameterSpec(keyAndIV[1])

        val encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.size)
        var aesCBC: Cipher? = null
        try {
            aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }

        try {
            Objects.requireNonNull<Cipher>(aesCBC).init(Cipher.DECRYPT_MODE, key, iv)
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        var decryptedData = ByteArray(0)
        try {
            decryptedData = aesCBC!!.doFinal(encrypted)
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }

        return String(decryptedData, StandardCharsets.UTF_8)
    }
}
