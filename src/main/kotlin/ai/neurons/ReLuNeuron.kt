package ai.neurons

import kotlin.math.max

class ReLuNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return max(0.0, vector)
    }
}