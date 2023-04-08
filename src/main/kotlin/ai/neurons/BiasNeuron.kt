package ai.neurons


class BiasNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return 1.0
    }
}