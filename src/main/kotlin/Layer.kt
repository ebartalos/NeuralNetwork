import neurons.Neuron

class Layer {

    var neurons: ArrayList<Neuron> = ArrayList()
    var outgoingConnections = ArrayList<Connection>()
    var incomingConnections = ArrayList<Connection>()

    fun addNeuron(neuron: Neuron) {
        neurons.add(neuron)
    }

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

    fun updateWeights(weights: MutableList<Double>, isOutgoing: Boolean) {
        val weightsIterator = weights.listIterator()
        val connections = if (isOutgoing) outgoingConnections else incomingConnections

        connections.onEach { it.weight = weightsIterator.next() }
    }

    fun weights(): ArrayList<Double> {
        val weights = ArrayList<Double>()
        for (connection in outgoingConnections) {
            weights.add(connection.weight)
        }
        return weights
    }
}