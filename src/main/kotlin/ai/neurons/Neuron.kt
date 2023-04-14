package ai.neurons

open class Neuron {

    var value = 1.0

    open fun activation(vector: Double): Double {
        return vector
    }

    fun calculate(vector: Double) {
        value = activation(vector)
    }

    open fun derivative(): Double {
        return 1.0
    }
}