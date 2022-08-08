import neurons.Neuron

class Layer {
    var neurons: ArrayList<Neuron> = ArrayList()

    fun addNeuron(neuron: Neuron) {
        neurons.add(neuron)
    }
}