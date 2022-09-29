package ai

import ai.neurons.BiasNeuron
import ai.neurons.Neuron
import ai.neurons.TanhNeuron
import java.util.*
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class Network(inputNeurons: Int, outputNeurons: Int, private val id: Int) {

    var layers = ArrayList<Layer>()

    init {
        val inputLayer = Layer()
        for (index in 1..inputNeurons) {
            inputLayer.addNeuron(Neuron())
        }
        inputLayer.addNeuron(BiasNeuron())
        layers.add(inputLayer)

        val outputLayer = Layer()
        for (index in 1..outputNeurons) {
            outputLayer.addNeuron(TanhNeuron())
        }
        layers.add(outputLayer)

    }

    fun <T : Any> addHiddenLayer(neuronType: KClass<T>, amountOfNeurons: Int, biasNeuron: Boolean = true) {
        val layer = Layer()
        for (index in 1..amountOfNeurons) {
            layer.addNeuron(neuronType.createInstance() as Neuron)
        }
        if (biasNeuron) layer.addNeuron(BiasNeuron())

        layers.add(layers.size - 1, layer)
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
            if (inputsIterator.hasNext()) {
                neuron.value = inputsIterator.next().toDouble()
            }
        }
    }

    /**
     * Creates connections between layers.
     * Has to be called after ALL layers are added.
     */
    fun createConnections(useHeHeuristics: Boolean) {
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

    private fun heHeuristics(previousLayerNeurons: Double): Double {
        val random = Random()
        return random.nextGaussian(0.0, sqrt(2.0 / previousLayerNeurons))
    }
}