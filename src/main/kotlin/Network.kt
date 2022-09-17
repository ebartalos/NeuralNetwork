import neurons.BiasNeuron
import neurons.Neuron
import neurons.SigmoidNeuron
import kotlin.math.sqrt

class Network(useHeHeuristics: Boolean = true) {

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

        createConnections(useHeHeuristics)
    }

    fun evaluate() {
        layers.forEach { it.evaluate() }
    }

    fun output(): ArrayList<Double> {
        val values = ArrayList<Double>()
        layers.last().neurons.forEach { values.add(it.value) }
        return values
    }

    fun updateWeights(weights: ArrayList<Double>) {
        var weightsIndex = 0
        for ((firstLayer, secondLayer) in layers.zipWithNext()) {
            val currentWeights = weights.subList(weightsIndex, weightsIndex + firstLayer.outgoingConnections.size)
            firstLayer.updateWeights(currentWeights, true)
            secondLayer.updateWeights(currentWeights, false)

            weightsIndex += firstLayer.outgoingConnections.size
        }
    }

    fun weights(): ArrayList<Double> {
        val weights = ArrayList<Double>()
        for (layer in layers) {
            weights.addAll(layer.weights())
        }
        return weights
    }

    fun setInputs(input1: Double, input2: Double) {
        layers.first().neurons[0].value = input1
        layers.first().neurons[1].value = input2
    }

    private fun createConnections(useHeHeuristics: Boolean) {
        for ((firstLayer, secondLayer) in layers.zipWithNext()) {
            for (outputNeuron in secondLayer.neurons) {
                for (inputNeuron in firstLayer.neurons) {
                    val connection = if (useHeHeuristics) {
                        Connection(inputNeuron, outputNeuron, heHeuristics(firstLayer.neurons.size.toDouble()))
                    } else {
                        Connection(inputNeuron, outputNeuron)
                    }
                    firstLayer.outgoingConnections.add(connection)
                    secondLayer.incomingConnections.add(connection)
                }
            }
        }
    }

    private fun heHeuristics(previousLayerNeurons: Double): Pair<Double, Double> {
        return Pair(-(1.0 / sqrt(previousLayerNeurons)), (1.0 / sqrt(previousLayerNeurons)))
    }

}