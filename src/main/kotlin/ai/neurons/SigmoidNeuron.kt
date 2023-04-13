package ai.neurons

import kotlin.math.exp

class SigmoidNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return if (vector >= 0) {
            val z = exp(-vector)
            1 / (1 + z)
        } else {
            val z = exp(vector)
            z / (1 + z)
        }
    }

    override fun derivative(value: Double): Double {
        return activation(value) * activation(1.0 - value)
    }
}