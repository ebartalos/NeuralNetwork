package ai.neurons

import kotlin.math.tanh

class TanhNeuron : Neuron() {

    override fun activationFunction(vector: Double): Double {
        return tanh(vector)
    }
}