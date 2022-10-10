package ai

import Constants
import ai.neurons.*
import java.io.File
import java.util.*
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Feed forward neural network.
 *
 * @param id of the network - used for debugging purposes
 */
class Network(private val id: Int) {

    var layers = ArrayList<Layer>()

    /**
     * Adds input layer to neural network.
     *
     * @param amountOfNeurons how many neurons should be added
     * @param biasNeuron if true, bias neuron will be added
     *                   if false, bias neuron will not be added
     */
    fun addInputLayer(amountOfNeurons: Int, biasNeuron: Boolean = true) {
        val inputLayer = Layer()
        for (index in 1..amountOfNeurons) {
            inputLayer.addNeuron(Neuron())
        }
        if (biasNeuron) inputLayer.addNeuron(BiasNeuron())
        layers.add(inputLayer)
    }

    /**
     * Adds output layer to neural network.
     *
     * @param neuronType type of neuron
     * @param amountOfNeurons how many neurons should be added
     */
    fun <T : Any> addOutputLayer(neuronType: KClass<T>, amountOfNeurons: Int) {
        val outputLayer = Layer()
        for (index in 1..amountOfNeurons) {
            outputLayer.addNeuron(neuronType.createInstance() as Neuron)
        }
        layers.add(outputLayer)
    }

    /**
     * Adds hidden layer to neural network.
     *
     * @param neuronType type of neuron
     * @param amountOfNeurons how many neurons should be added
     * @param biasNeuron if true, bias neuron will be added
     *                   if false, bias neuron will not be added
     */
    fun <T : Any> addHiddenLayer(neuronType: KClass<T>, amountOfNeurons: Int, biasNeuron: Boolean = true) {
        val layer = Layer()
        for (index in 1..amountOfNeurons) {
            layer.addNeuron(neuronType.createInstance() as Neuron)
        }
        if (biasNeuron) layer.addNeuron(BiasNeuron())

        layers.add(layers.size - 1, layer)
    }

    /**
     * Evaluates the whole network, e.g. calculate value of each neuron in each layer,
     * starting from input and forward via hidden to outputs
     */
    fun evaluate() {
        layers.forEach { it.evaluate() }
    }

    /**
     * Returns values of output neurons.
     */
    fun output(): ArrayList<Double> {
        val values = ArrayList<Double>()
        layers.last().neurons.forEach { values.add(it.value) }
        return values
    }

    /**
     * Updates values of weights.
     *
     * @param weights new values
     */
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
    fun createConnections() {
        for ((firstLayer, secondLayer) in layers.zipWithNext()) {
            for (outputNeuron in secondLayer.neurons) {
                if (outputNeuron is BiasNeuron) continue

                for (inputNeuron in firstLayer.neurons) {
                    val connection =
                        if (inputNeuron is BiasNeuron) {
                            Connection(inputNeuron, outputNeuron, listOf(0.2, -0.2).random())
                        } else if (outputNeuron is ReLuNeuron) {
                            Connection(inputNeuron, outputNeuron, heHeuristics(firstLayer.neurons.size))
                        } else if ((outputNeuron is TanhNeuron) || (outputNeuron is SigmoidNeuron)) {
                            Connection(inputNeuron, outputNeuron, xavierHeuristics(firstLayer.neurons.size))
                        } else {
                            Connection(inputNeuron, outputNeuron)
                        }

                    firstLayer.outgoingConnections.add(connection)
                    secondLayer.incomingConnections.add(connection)
                }
            }
        }
    }

    /**
     * Default for ReLu neurons.
     */
    private fun heHeuristics(previousLayerNeurons: Int): Double {
        val random = Random()
        return random.nextGaussian(0.0, sqrt(2.0 / previousLayerNeurons.toDouble()))
    }

    /**
     * Default for Tanh and Sigmoid neurons.
     */
    private fun xavierHeuristics(previousLayerNeurons: Int): Double {
        val random = Random()
        return random.nextDouble(
            -(1 / sqrt(previousLayerNeurons.toDouble())),
            1 / sqrt(previousLayerNeurons.toDouble())
        )
    }

    fun saveWeightsToFile(file: File = File(Constants.WEIGHTS_FILE), overwrite: Boolean = false) {
        if (overwrite) file.writeText("")

        // add network information - type of neurons and count
        for (layer in layers) {
            file.appendText("--Layer--\n")
            val neuronCount = layer.neurons.groupingBy { it.javaClass }.eachCount()
            neuronCount.forEach { (k, v) -> file.appendText("${k}:${v}\n") }
        }

        file.appendText("Weights:\n")
        weights().forEach {
            file.appendText("$it\n")
        }
    }

    fun loadWeightsFromFile() {
        val weightsFile = File(Constants.WEIGHTS_FILE)
        val weights = ArrayList<Double>()
        weightsFile.readLines().forEach {
            if (!(it.contains("class") || it.contains("Layer") or it.contains("eights"))) {
                weights.add(it.toDouble())
            }
        }
        updateWeights(weights)
    }
}