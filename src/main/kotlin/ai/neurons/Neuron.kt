package ai.neurons

open class Neuron {

    var value = 1.0

    open fun activationFunction(vector: Double): Double {
        return vector
    }

    fun calculate(vector: Double) {
        value = activationFunction(vector)
    }
}