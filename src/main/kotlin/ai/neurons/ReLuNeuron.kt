package ai.neurons

import kotlin.math.max

class ReLuNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return max(0.0, vector)
    }
}