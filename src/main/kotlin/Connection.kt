import neurons.Neuron

class Connection constructor(inputLayer: Layer, private var outputNeuron: Neuron) {
    var weights = HashMap<Neuron, Double>()

    init {
        for (neuron in inputLayer.neurons) {
            weights[neuron] = Math.random()
        }
    }

    fun evaluate() {
        var sum = 0.0
        for ((neuron, weight) in weights) {
            sum += neuron.value * weight
        }
        outputNeuron.calculate(sum)
    }
}