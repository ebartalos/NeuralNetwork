import neurons.Neuron

class Connection constructor(var inputNeuron: Neuron, var outputNeuron: Neuron) {
    var weight: Double = Math.random()

    // backpropagation part
    var error: Double? = null
}