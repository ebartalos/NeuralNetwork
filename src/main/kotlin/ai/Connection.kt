package ai

import ai.neurons.Neuron
import java.util.*

class Connection(
    var inputNeuron: Neuron,
    var outputNeuron: Neuron,
    weightsRange: Pair<Double, Double>? = null
) {
    private val random = Random()

    var weight: Double =
        if (weightsRange != null) random.nextGaussian(weightsRange.first, weightsRange.second) else Math.random()

    // backpropagation part
    var error: Double? = null
}