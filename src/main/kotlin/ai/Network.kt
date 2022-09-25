package ai

import ai.neurons.BiasNeuron
import ai.neurons.Neuron
import ai.neurons.SigmoidNeuron
import kotlin.math.sqrt

class Network(inputNeurons: Int = 2, outputNeurons: Int = 1, useHeHeuristics: Boolean = true) {

    var layers = ArrayList<Layer>()

    init {
        // input layer
        val layer1 = Layer()
        for (index in 1..inputNeurons) {
            layer1.addNeuron(Neuron())
        }
        layers.add(layer1)

        // hidden layer
        val layer2 = Layer()
        for (index in 1..(inputNeurons + outputNeurons) * 2 / 3) {
            layer2.addNeuron(SigmoidNeuron())
        }
        layer2.addNeuron(BiasNeuron())
        layers.add(layer2)

        // output layer
        val layer3 = Layer()
        for (index in 1..outputNeurons) {
            layer3.addNeuron(SigmoidNeuron())
        }
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

    fun setInputs(inputs: ArrayList<Int>) {
        val inputsIterator = inputs.listIterator()

        for (neuron in layers.first().neurons) {
            neuron.value = inputsIterator.next().toDouble()
        }
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