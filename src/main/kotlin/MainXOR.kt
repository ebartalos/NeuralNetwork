import ai.Network
import ai.neurons.Neuron
import ai.neurons.SigmoidNeuron


object MainXOR {
    @JvmStatic
    fun main(args: Array<String>) {
        val network = Network()

        network.addInputLayer(2, true)
        network.addHiddenLayer(SigmoidNeuron::class, 2, true)
        network.addOutputLayer(Neuron::class, 1)
        network.createConnections()

        val learningRate = 0.2

        val inputVector = ArrayList<Pair<Double, Double>>()
        val outputVector = ArrayList<Double>()

        inputVector.add(Pair(0.0, 0.0))
        outputVector.add(0.0)

        inputVector.add(Pair(1.0, 1.0))
        outputVector.add(0.0)

        inputVector.add(Pair(0.0, 1.0))
        outputVector.add(1.0)

        inputVector.add(Pair(1.0, 0.0))
        outputVector.add(1.0)

        for (epoch in 0..100000) {
            for (index in 0 until inputVector.size) {
                network.setInputs(arrayListOf(inputVector[index].first, inputVector[index].second))
                network.propagate()

                network.backpropagate(listOf(outputVector[index]), learningRate)
            }
        }

        for (i in 0 until inputVector.size) {
            network.setInputs(arrayListOf(inputVector[i].first, inputVector[i].second))
            network.propagate()
            println(
                "${inputVector[i].first}, ${inputVector[i].second}, ${outputVector[i]} = " + network.output().first()
                    .toString()
            )
        }
    }
}