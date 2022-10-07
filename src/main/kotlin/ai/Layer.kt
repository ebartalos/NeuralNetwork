package ai

import ai.neurons.Neuron

/**
 * TODO
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
     * TODO
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
     * Changes weights' value to specified one.
     *
     * @param weights desired weights' values
     * @param isOutgoing TODO
     */
    fun updateWeights(weights: MutableList<Double>, isOutgoing: Boolean) {
        val weightsIterator = weights.listIterator()
        val connections = if (isOutgoing) outgoingConnections else incomingConnections

        connections.onEach { it.weight = weightsIterator.next() }
    }

    /**
     * Returns all weights of layer.
     */
    fun weights(): ArrayList<Double> {
        val weights = ArrayList<Double>()
        for (connection in outgoingConnections) {
            weights.add(connection.weight)
        }
        return weights
    }
}