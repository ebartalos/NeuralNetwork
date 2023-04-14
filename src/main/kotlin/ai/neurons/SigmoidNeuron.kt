package ai.neurons

import kotlin.math.exp

class SigmoidNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return if (vector >= 0) {
            1 / (1 + exp(-vector))
        } else {
            val z = exp(vector)
            z / (1 + z)
        }
    }

    override fun derivative(): Double {
        return activation(value) * activation(1.0 - value)
    }
}