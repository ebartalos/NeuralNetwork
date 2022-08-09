import neurons.Neuron
import neurons.SigmoidNeuron

class Network {
    private var layers = ArrayList<Layer>()
    private var connections = ArrayList<Connection>()

    init {
        // input layers
        val layer1 = Layer()
        layer1.addNeuron(Neuron())
        layer1.addNeuron(Neuron())
        layers.add(layer1)

        // hidden layer
        val layer2 = Layer()
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(SigmoidNeuron())
        layers.add(layer2)

        // output layer
        val layer3 = Layer()
        layer3.addNeuron(SigmoidNeuron())
        layers.add(layer3)

        createConnections()
    }

    fun evaluate() {
        connections.forEach { it.evaluate() }
    }

    fun output(): ArrayList<Double> {
        val values = ArrayList<Double>()
        layers.last().neurons.forEach { values.add(it.value) }
        return values
    }

    private fun createConnections() {
        lateinit var inputLayer: Layer

        for (layer in layers) {
            if (layer == layers[0]) {
                inputLayer = layer
                continue
            } else {
                for (neuron in layer.neurons) {
                    connections.add(Connection(inputLayer, neuron))
                    inputLayer = layer
                }
            }
        }
    }
}