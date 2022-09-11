import neurons.BiasNeuron
import neurons.Neuron
import neurons.SigmoidNeuron

class Network {

    var layers = ArrayList<Layer>()

    init {
        // input layer
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

    fun setInputs(input1: Double, input2: Double) {
        layers.first().neurons[0].value = input1
        layers.first().neurons[1].value = input2
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
}