package ai.neurons


class BiasNeuron : Neuron() {

    override fun activationFunction(vector: Double): Double {
        return 1.0
    }
}