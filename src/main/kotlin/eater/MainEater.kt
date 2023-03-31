package eater

import Constants
import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object MainEater {

    /**
     * TRAIN - trains network
     * TEST - load weights from file and test network in GUI
     */
    enum class Activity {
        TRAIN, TEST
    }

    private val activity: Activity = Activity.TEST
    private const val MAX_FITNESS = 10000000

    @JvmStatic
    fun main(args: Array<String>) {
        when (activity) {
            Activity.TRAIN -> train()
            Activity.TEST -> test()
        }
    }

    /**
     * Train networks until max fitness (or max generatio limit) is reached.
     */
    private fun train() {
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()

        setNetworks(networks, fitness)

        var sortedFitness: Map<Network, Int>
        var bestFitness = 0

        for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                val fitnessOfNetwork = playGame(network, MAX_FITNESS)
                fitness[network] = fitnessOfNetwork
                if (fitnessOfNetwork >= MAX_FITNESS) break
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
                playGame(bestNetwork, MAX_FITNESS)
            }

            val time = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            println("$time Gen $generation best gen fitness ${fitness[bestNetwork]!!} ATH fitness $bestFitness")

            if (bestFitness >= MAX_FITNESS) {
                println("TRAINING FINISHED! SCORE IS $bestFitness")
                return
            }
        }
    }

    /**
     * Set neurons, weights and connections in neural networks.
     *
     * @param networks list of shell empty networks
     * @param fitness assign default fitness value to all networks
     *
     */
    private fun setNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        for (networkId in 1..Constants.MAX_NEURAL_NETWORKS) {
            lateinit var network: Network

            if (Constants.LOAD_NETWORK_FILE_ON_START) {
                network = Network(networkId)
                network.loadTrainedNetworkFromFile()
            } else {
                network = createNetwork(networkId)
            }

            networks.add(network)
            fitness[network] = 0
        }
    }

    /**
     * Create 1 neural network.
     *
     * @param id unique id
     *
     * @return neural network
     */
    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(8)
        network.addHiddenLayer(ReLuNeuron::class, 10, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    /**
     *
     * @param network neural network
     * @param maxFitness upper limit for training
     *
     * @return fitness
     */
    private fun playGame(network: Network, maxFitness: Int): Int {
        val eater = Eater()
        return eater.play(network, maxFitness)
    }

    /**
     * Load weights from file and test network in GUI.
     */
    private fun test() {
        val network = Network(1)
        network.loadTrainedNetworkFromFile()
        val eater = Eater()
        eater.play(network, MAX_FITNESS, useGUI = true)
    }
}