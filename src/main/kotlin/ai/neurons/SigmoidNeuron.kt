package ai.neurons

import kotlin.math.exp

class SigmoidNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return if (vector >= 0) {
            val z = exp(-vector)
            1 / (1 + z)
        } else {
            val z = exp(vector)
            z / (1 + z)
        }
    }
}