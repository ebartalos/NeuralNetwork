package ai

import ai.neurons.Neuron

/**
 * @param weightHeuristic if true, set weight by passed heuristic, if false, set weight randomly
 */
class Connection(
    var inputNeuron: Neuron,
    var outputNeuron: Neuron,
    weightHeuristic: Double? = null
) {
    var weight: Double = weightHeuristic ?: Math.random()

    // backpropagation part
    var error: Double? = null
}