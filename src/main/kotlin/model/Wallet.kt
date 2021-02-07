package model

import java.lang.IllegalArgumentException
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey

data class Wallet(val publicKey: PublicKey, val privateKey: PrivateKey, val blockChain: BlockChain) {

    companion object {
        fun create(blockChain: BlockChain): Wallet {
            val generator = KeyPairGenerator.getInstance("RSA")
            generator.initialize(2048)
            val keyPair = generator.generateKeyPair()

            return Wallet(keyPair.public, keyPair.private, blockChain)
        }
    }

    fun sendFundsTo(recipient: PublicKey, amountToSend: Int): Transaction {
        if (amountToSend > balance) {
            throw IllegalArgumentException("Insufficient funds")
        }

        val transaction = Transaction(sender = publicKey, recipient = publicKey, amount = amountToSend)
        transaction.outputs.add(TransactionOutput(recipient, amountToSend, transaction.hash))

        var collectedAmount = 0
        for (myTransaction in getMyTransactions()) {
            collectedAmount += myTransaction.amount
            transaction.inputs.add(myTransaction)

            if (collectedAmount > amountToSend) {
                val change = collectedAmount - amountToSend
                transaction.outputs.add(
                    TransactionOutput(
                        recipient = publicKey,
                        amount = change,
                        transactionHash = transaction.hash
                    )
                )
            }

            if (collectedAmount >= amountToSend) {
                break
            }
        }
        return transaction.sign(privateKey)
    }

    val balance: Int
        get() = getMyTransactions().sumBy { it.amount }

    private fun getMyTransactions(): Collection<TransactionOutput> {
        return blockChain.UTXO.filterValues { it.isMine(publicKey) }.values
    }
}