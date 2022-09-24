package ai.neurons

import kotlin.math.abs

class AmplifyNeuron(private val exponent: Double, private val addition: Double) : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return (abs(vector) * exponent) + addition
    }
}