package algorithms

import Network

class Backpropagation(private val network: Network, private val target: Double, private val learningRate: Double) {

    fun backpropagate() {
        determineErrors()
        updateWeights()
    }

    private fun determineErrors() {
        fun derivative(value: Double): Double {
            return value * (1.0 - value)
        }

        val transferDerivative = derivative(network.output()[0])
        val outputError = transferDerivative * (network.output()[0] - target)

        // output layer
        network.layers.last().incomingConnections.forEach {
            it.error = outputError
        }

        // hidden layer
        network.layers[1].incomingConnections.forEach {
            it.error = (it.weight * outputError) * it.outputNeuron.derivative()
        }
    }

    private fun updateWeights() {
        for (layer in network.layers.reversed()) {
            for (connection in layer.incomingConnections) {
                connection.weight -= learningRate * connection.error!! * connection.inputNeuron.value
            }
        }
    }
}