package ai

import ai.neurons.Neuron

/**
 * Representation of connection for feed-forward neural network.
 * Consists of two neurons and weight of connection.
 *
 * @param inputNeuron neuron where connection starts
 * @param outputNeuron neuron where connection ends
 * @param weightHeuristic if true, set weight by passed heuristic, if false, set weight randomly
 */
class Connection(
    var inputNeuron: Neuron,
    var outputNeuron: Neuron,
    weightHeuristic: Double? = null
) {
    var weight: Double = weightHeuristic ?: Math.random()
}