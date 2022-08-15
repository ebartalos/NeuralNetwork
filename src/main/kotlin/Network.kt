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

    fun backpropagation(output: Double) {
        fun derivate(value: Double): Double {
            return value * (1.0 - value)
        }

        val target = 0.0
        val outputError = derivate(output) * (output - target)
        val errors = ArrayList<Double>()
    }
    // calculate errors
//        for(weight in connections.last().weights){
//            println("old weight ${weight.value}")
//                weight.setValue(abs((deltaOutputSum / weight.value) + weight.value))
//                println("new weight ${weight.value}")
//        }

//        val oldWeights = ArrayList<Double>()
//        val hiddenSum = layers[2].sum()
//
//        for (connection in connections.subList(3,4)){
//            for (weight in connection.weights){
//                println("old weight ${weight.value}")
//                oldWeights.add(weight.value)
//                weight.setValue(abs((deltaOutputSum / weight.value) + weight.value))
//                println("new weight ${weight.value}")
//            }
//        }
//
//        val deltaHiddenSum = ArrayList<Double>()
//
//        for (oldWeight in oldWeights){
//            deltaHiddenSum.add((deltaOutputSum / oldWeight) * )
//        }
//
//
//        for (connection in connections.subList(0,3)){
//            for (weight in connection.weights){
//                weight.setValue(abs((deltaOutputSum / weight.value * layers.last().neurons[0].value) + weight.value))
//            }
//        }
//    }
}