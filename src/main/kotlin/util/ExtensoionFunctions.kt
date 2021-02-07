package util

import java.math.BigInteger
import java.security.*
import java.util.*

fun String.hash(algorithm: String = "SHA-256"): String {
    MessageDigest.getInstance(algorithm).also {
        it.update(this.toByteArray())
        return String.format("%064x", BigInteger(1, it.digest()))
    }
}

fun String.sign(privateKey: PrivateKey, algorithm: String = "SHA256withRSA"): ByteArray {
    Signature.getInstance(algorithm).also {
        it.initSign(privateKey)
        it.update(this.toByteArray())
        return it.sign()
    }
}

fun String.verifySignature(publicKey: PublicKey, signature: ByteArray, algorithm: String = "SHA256withRSA"): Boolean {
    Signature.getInstance(algorithm).also {
        it.initVerify(publicKey)
        it.update(this.toByteArray())
        return it.verify(signature)
    }
}

fun Key.encodeToString(): String {
    return Base64.getEncoder().encodeToString(this.encoded)
}