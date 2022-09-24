package ai.neurons

import kotlin.math.abs

class AmplifyNeuron(private val rate: Double) : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return abs(vector) * rate
    }
}