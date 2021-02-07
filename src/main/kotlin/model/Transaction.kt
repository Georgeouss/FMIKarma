package model

import util.encodeToString
import util.hash
import util.sign
import util.verifySignature
import java.security.PrivateKey
import java.security.PublicKey

data class TransactionOutput(
    val recipient: PublicKey,
    val amount: Int,
    val transactionHash: String,
    var hash: String = ""
) {

    init {
        hash = "${recipient.encodeToString()}$amount$transactionHash".hash()
    }

    fun isMine(me: PublicKey) = recipient == me
}

data class Transaction(
    val sender: PublicKey,
    val recipient: PublicKey,
    val amount: Int,
    var hash: String = "",
    val inputs: MutableList<TransactionOutput> = mutableListOf(),
    val outputs: MutableList<TransactionOutput> = mutableListOf()
) {

    companion object {
        var salt: Long = 0
            get() {
                return ++field
            }
    }

    init {
        hash = "${sender.encodeToString()}${recipient.encodeToString()}$amount$salt"
    }

    private var signature: ByteArray = ByteArray(0)

    fun sign(privateKey: PrivateKey): Transaction {
        signature = "${sender.encodeToString()}${recipient.encodeToString()}$amount".sign(privateKey)
        return this
    }

    fun isSignatureValid(): Boolean {
        return "${sender.encodeToString()}${recipient.encodeToString()}$amount".verifySignature(sender, signature)
    }
}