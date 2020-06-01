package com.capricorn.baxims.utils

import android.util.Base64
import com.capricorn.baxims.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtils {

    fun encryption(value:String): String {

        val iv = IvParameterSpec(BuildConfig.initVector.toByteArray(charset("UTF-8")))
        val skeySpec = SecretKeySpec(BuildConfig.key.toByteArray(charset("UTF-8")), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(value.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)

    }

    fun decryption(value:String): String {

        val iv = IvParameterSpec(BuildConfig.initVector.toByteArray(charset("UTF-8")))
        val skeySpec = SecretKeySpec(BuildConfig.key.toByteArray(charset("UTF-8")), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        val byte =  Base64.decode(value, Base64.DEFAULT)
        val encrypted = cipher.doFinal(byte)
        return String(encrypted)

    }

}