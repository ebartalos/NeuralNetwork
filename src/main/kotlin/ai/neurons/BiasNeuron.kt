package ai.neurons


class BiasNeuron : Neuron() {

    private val biasValue = 1.0

    override fun activation(vector: Double): Double {
        return biasValue
    }

    override fun derivative(value: Double): Double {
        return biasValue
    }
}