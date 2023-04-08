package ai

import ai.neurons.Neuron

/**
 * Representation of layer in feed forward neural networks
 */
class Layer {

    var neurons: ArrayList<Neuron> = ArrayList()
    var outgoingConnections = ArrayList<Connection>()
    var incomingConnections = ArrayList<Connection>()

    /**
     * Add neuron to layer.
     *
     * @param neuron Neuron
     */
    fun addNeuron(neuron: Neuron) {
        neurons.add(neuron)
    }

    /**
     * Calculate value of each neuron in the layer.
     */
    fun evaluate() {
        if (incomingConnections.isNotEmpty()) {
            for (neuron in neurons) {
                var sum = 0.0
                for (connection in incomingConnections) {
                    if (connection.outputNeuron == neuron) {
                        sum += connection.inputNeuron.value * connection.weight
                    }
                }
                neuron.calculate(sum)
            }
        }
    }

    /**
     * Change weights' value to specified one.
     *
     * @param weights desired weights' values
     * @param isOutgoing if true, update outgoing connections
     *                   if false, update incoming connections
     */
    fun updateWeights(weights: MutableList<Double>, isOutgoing: Boolean) {
        val weightsIterator = weights.listIterator()
        val connections = if (isOutgoing) outgoingConnections else incomingConnections

        connections.onEach { it.weight = weightsIterator.next() }
    }

    /**
     * Return all weights of layer.
     */
    fun weights(): ArrayList<Double> {
        val weights = ArrayList<Double>()
        for (connection in outgoingConnections) {
            weights.add(connection.weight)
        }
        return weights
    }
}