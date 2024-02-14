package ai

import eater.Constants
import ai.neurons.*
import java.io.File
import java.util.*
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


/**
 * Feed forward neural network.
 */
class Network {

    var layers = ArrayList<Layer>()

    /**
     * Add input layer to neural network.
     *
     * @param numberOfNeurons how many neurons should be added
     * @param biasNeuron if true, bias neuron will be added
     *                   if false, bias neuron will not be added
     */
    fun addInputLayer(numberOfNeurons: Int, biasNeuron: Boolean = true) {
        val inputLayer = Layer()
        for (index in 1..numberOfNeurons) {
            inputLayer.addNeuron(Neuron())
        }
        if (biasNeuron) inputLayer.addNeuron(BiasNeuron())
        layers.add(inputLayer)
    }

    /**
     * Add output layer to neural network.
     *
     * @param neuronType type of neuron
     * @param numberOfNeurons how many neurons should be added
     */
    fun <T : Any> addOutputLayer(neuronType: KClass<T>, numberOfNeurons: Int) {
        val outputLayer = Layer()
        for (index in 1..numberOfNeurons) {
            outputLayer.addNeuron(neuronType.createInstance() as Neuron)
        }
        layers.add(outputLayer)
    }

    /**
     * Add hidden layer to neural network.
     *
     * @param neuronType type of neuron
     * @param numberOfNeurons how many neurons should be added
     * @param biasNeuron if true, bias neuron will be added
     *                   if false, bias neuron will not be added
     */
    fun <T : Any> addHiddenLayer(neuronType: KClass<T>, numberOfNeurons: Int, biasNeuron: Boolean = true, dropout:Double=0.0) {
        val layer = Layer()
        for (index in 1..numberOfNeurons) {
            layer.addNeuron(neuronType.createInstance() as Neuron)
        }

        if (biasNeuron) layer.addNeuron(BiasNeuron())
        if (dropout != 0.0) layer.dropout(dropout)

        layers.add(layer)
    }

    /**
     * Forward propagate the whole network, e.g. calculate value of each neuron in each layer,
     * starting from input and forward via hidden to outputs
     */
    fun propagate() {
        layers.forEach { it.evaluate() }
    }

    /**
     * Backpropagate the whole network and adjust weights based by error and learning rate.
     * Has to be called after forward propagation is done!
     *
     * @param expectedOutputs expected outputs
     * @param learningRate learning rate
     */
    fun backpropagate(expectedOutputs: List<Double>, learningRate: Double) {
        val errors = arrayListOf<Double>()
        val expectedOutputsIterator = expectedOutputs.iterator()

        for (output in output()) {
            errors.add(expectedOutputsIterator.next() - output)
        }

        for (outputError in errors) {
            // calculate error gradients
            val gradients = mutableListOf<Double>()
            for (reversedLayer in layers.reversed()) {
                for (connection in reversedLayer.incomingConnections) {
                    val gradient =
                        learningRate * outputError * connection.inputNeuron.value * connection.outputNeuron.derivative()
                    gradients.add(gradient)
                }
            }

            // update weights
            val gradientsIterator = gradients.iterator()
            for (reversedLayer in layers.reversed()) {
                for (connection in reversedLayer.incomingConnections) {
                    connection.weight += gradientsIterator.next()
                }
            }
        }
    }

    /**
     * Return values of output neurons.
     */
    fun output(): ArrayList<Double> {
        val values = ArrayList<Double>()
        layers.last().neurons.forEach { values.add(it.value) }
        return values
    }

    /**
     * Return value of output neurons after softmax is applied
     *
     * @param chunked number of neurons to softmax together - default is the whole output layer
     */
    fun softmaxOutput(chunked: Int = layers.last().neurons.size): ArrayList<Double> {
        val values = ArrayList<Double>()
        val output = output().chunked(chunked)

        for (chunkedOutput in output) {
            for (neuronValue in chunkedOutput) {
                values.add(softmax(neuronValue, chunkedOutput.toDoubleArray()))
            }
        }

        return values
    }

    private fun softmax(input: Double, neuronValues: DoubleArray): Double {
        val total = Arrays.stream(neuronValues).map { a: Double -> exp(a) }.sum()
        return exp(input) / total
    }

    /**
     * Update values of weights.
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

    /**
     * Weights' values
     */
    fun weights(): ArrayList<Double> {
        val weights = ArrayList<Double>()
        for (layer in layers) {
            weights.addAll(layer.weights())
        }
        return weights
    }

    /**
     * Set inputs for the network.
     *
     * @param inputs list of values
     */
    fun setInputs(inputs: ArrayList<Double>) {
        val inputsIterator = inputs.listIterator()

        for (neuron in layers.first().neurons) {
            if (inputsIterator.hasNext()) {
                neuron.value = inputsIterator.next()
            }
        }
    }

    /**
     * Create connections between layers.
     * Has to be called after ALL layers are added.
     */
    fun createConnections() {
        for ((firstLayer, secondLayer) in layers.zipWithNext()) {
            for (outputNeuron in secondLayer.neurons) {
                if (outputNeuron is BiasNeuron) continue

                for (inputNeuron in firstLayer.neurons) {
                    createConnection(firstLayer, secondLayer, inputNeuron, outputNeuron)
                }
            }
        }
    }

    /**
     * Add 1 connection between neurons.
     */
    private fun createConnection(firstLayer: Layer, secondLayer: Layer, inputNeuron: Neuron, outputNeuron: Neuron) {
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

    /**
     * Default for ReLu neurons.
     */
    private fun heHeuristics(previousLayerNeurons: Int): Double {
        return Random().nextGaussian(0.0, sqrt(2.0 / previousLayerNeurons.toDouble()))
    }

    /**
     * Default for Tanh and Sigmoid neurons.
     */
    private fun xavierHeuristics(previousLayerNeurons: Int): Double {
        return Random().nextDouble(
            -(1 / sqrt(previousLayerNeurons.toDouble())),
            1 / sqrt(previousLayerNeurons.toDouble())
        )
    }

    /**
     * Save network details (layers, number/types of neurons, weights' values) to the file.
     *
     * @param file target file for saving
     * @param generation current generation of training
     */
    fun saveTrainedNetworkToFile(file: File = File(Constants.BEST_NETWORK_FILE), generation: Int = 0) {
        file.writeText("Generation:$generation\n")

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

    /**
     * Load saved network (from method saveTrainedNetworkToFile) to the network.
     *
     * @param file file with saved network
     */
    fun loadTrainedNetworkFromFile(file: File = File(Constants.BEST_NETWORK_FILE)) {
        val weights = ArrayList<Double>()
        val networkStructure = mutableListOf<String>()

        for (line in file.readLines()) {
            if (line.contains("Generation")) {
                continue
            }
            if (line.contains("Weights")) {
                break
            } else if (line.contains("Layer").not()) {
                networkStructure.add(line)
            }
        }

        val neuronClasses = hashMapOf(
            "ReLuNeuron" to ReLuNeuron::class,
            "TanhNeuron" to TanhNeuron::class,
            "SigmoidNeuron" to SigmoidNeuron::class,
            "Neuron" to Neuron::class
        )

        for (index in 0 until networkStructure.size) {
            var neuronTypeAndAmount = networkStructure[index].split(":")
            neuronTypeAndAmount = neuronTypeAndAmount[0].split(" ") + neuronTypeAndAmount[1]
            val neuronType = neuronTypeAndAmount[1].replace("ai.neurons.", "")
            val neuronAmount = Integer.parseInt(neuronTypeAndAmount[2])

            if (index == 0) { // input layer
                val biasNeuron = networkStructure[1].contains("Bias")
                addInputLayer(neuronAmount, biasNeuron = biasNeuron)
            } else if (index == networkStructure.size - 1) { // output layer
                addOutputLayer(neuronClasses.getValue(neuronType), neuronAmount)
            } else if (networkStructure[index].contains("Bias").not()) { // hidden layers
                val biasNeuron = networkStructure[index + 1].contains("Bias")
                addHiddenLayer(neuronClasses.getValue(neuronType), neuronAmount, biasNeuron = biasNeuron)
            }
        }

        createConnections()

        file.readLines().forEach {
            // load weights
            if (!(it.contains("class") || it.contains("Layer") or it.contains("Weights") or it.contains("Generation"))) {
                weights.add(it.toDouble())
            }
        }
        updateWeights(weights)
    }
}