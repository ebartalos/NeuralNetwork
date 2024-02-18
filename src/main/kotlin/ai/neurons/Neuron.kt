package ai.neurons

open class Neuron {

    var isDropped = false
    var value = 1.0

    open fun activation(vector: Double): Double {
        return vector
    }

    /**
     * Calculate neuron's value.
     *
     * @param vector of values coming in
     */
    fun calculate(vector: Double) {
        value = if (isDropped) {
            0.0
        } else {
            activation(vector)
        }
    }

    open fun derivative(): Double {
        return 1.0
    }
}