import neurons.Connection
import neurons.Neuron
import neurons.SigmoidNeuron

class Network {
    private var layers = ArrayList<Layer>()
    private var connections = ArrayList<Connection>()

    init {
        val layer1 = Layer()
        layer1.addNeuron(Neuron())
        layer1.addNeuron(Neuron())
        layers.add(layer1)

        val layer2 = Layer()
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(SigmoidNeuron())
        layer2.addNeuron(SigmoidNeuron())
        layers.add(layer2)

        createConnections()

        connections.forEach { it.evaluate() }

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