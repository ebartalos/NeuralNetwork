import neurons.BiasNeuron
import neurons.Neuron
import neurons.SigmoidNeuron

class Network {
    private var layers = ArrayList<Layer>()

    init {
        // input layers
        val layer1 = Layer()
        layer1.addNeuron(Neuron())
        layer1.addNeuron(Neuron())
        layer1.addNeuron(BiasNeuron())
        layers.add(layer1)

        // hidden layer
        val layer2 = Layer()
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(BiasNeuron())
        layers.add(layer2)

        // output layer
        val layer3 = Layer()
        layer3.addNeuron(SigmoidNeuron())
        layers.add(layer3)

        createConnections()
    }

    fun evaluate() {
        layers.forEach { it.evaluate() }
    }

    fun output(): ArrayList<Double> {
        val values = ArrayList<Double>()
        layers.last().neurons.forEach { values.add(it.value) }
        return values
    }

    private fun createConnections() {
        for ((firstLayer, secondLayer) in layers.zipWithNext()) {
            for (outputNeuron in secondLayer.neurons) {
                for (inputNeuron in firstLayer.neurons) {
                    val connection = Connection(inputNeuron, outputNeuron)
                    firstLayer.outgoingConnections.add(connection)
                    secondLayer.incomingConnections.add(connection)
                }
            }
        }
    }

    fun backpropagation() {
        fun derivative(value: Double): Double {
            return value * (1.0 - value)
        }

        val target = 0.0
        val transferDerivative = derivative(output()[0])
        val outputError = transferDerivative * (output()[0] - target)

        // output layer
        layers.last().incomingConnections.forEach {
            it.error = outputError
        }

        // hidden layer
        layers[1].incomingConnections.forEach {
            it.error = (it.weight * outputError) * derivative(it.outputNeuron.value)
        }
    }
}