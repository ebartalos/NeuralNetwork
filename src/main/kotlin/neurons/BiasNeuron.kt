package neurons


class BiasNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return 1.0
    }
}