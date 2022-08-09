package neurons

import kotlin.math.abs

class AmplifyNeuron : Neuron() {

    override fun activationMethod(vector: Double): Double {
        return abs(vector) * 5
    }
}