package ai.neurons

class ReLuNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return if (vector > 0) vector
        else 0.0
    }
}