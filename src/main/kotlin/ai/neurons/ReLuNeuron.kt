package ai.neurons

import kotlin.math.max

class ReLuNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return max(0.0, vector)
    }

    override fun derivative(value: Double): Double {
        return if (value <= 0) 0.0 else 1.0
    }
}