package ai.neurons

import kotlin.math.tanh

class TanhNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return tanh(vector)
    }
}