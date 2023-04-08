package ai.neurons

import kotlin.math.tanh

class TanhNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return tanh(vector)
    }
}