package eater

import Constants
import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import kotlinx.coroutines.*
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

    // artificial high number
    private const val MAX_FITNESS = 10000000

    @JvmStatic
    fun main(args: Array<String>) {
        when (activity) {
            Activity.TRAIN -> train()
            Activity.TEST -> test()
        }
    }

    /**
     * Train networks until max fitness (or max generation limit) is reached.
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

            when (Constants.TRAIN_IN_PARALLEL) {
                true -> trainInParallel(networks, fitness)
                false -> trainSequentially(networks, fitness)
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
            }

            if (generation % 100 == 0) {
                val time = DateTimeFormatter
                    .ofPattern("HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.now())
                println("$time Gen $generation best gen fitness ${fitness[bestNetwork]!!} ATH fitness $bestFitness")
            }

            if (bestFitness >= MAX_FITNESS) {
                println("TRAINING FINISHED! SCORE IS $bestFitness")
                return
            }
        }
    }

    /**
     * Train networks in parallel using coroutines - faster, but exhausting
     *
     * @param networks all networks
     * @param fitness all networks' fitness
     */
    private fun trainInParallel(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        val networkIterator = networks.iterator()

        runBlocking {
            val tasks = mutableListOf<Deferred<Unit>>()
            do {
                val network = networkIterator.next()
                tasks.add(async(Dispatchers.Default) {
                    fitness[network] = playGame(network)
                })
            } while (networkIterator.hasNext())

            tasks.awaitAll()
        }
    }

    /**
     * Train networks in single thread - slower, but less exhausting
     *
     * @param networks all networks
     * @param fitness all networks' fitness
     */
    private fun trainSequentially(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        for (network in networks) {
            val fitnessOfNetwork = playGame(network)
            fitness[network] = fitnessOfNetwork
            if (fitnessOfNetwork >= MAX_FITNESS) return
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
                network = Network()
                network.loadTrainedNetworkFromFile()
            } else {
                network = createNetwork()
            }

            networks.add(network)
            fitness[network] = 0
        }
    }

    /**
     * Create 1 neural network.
     **
     * @return neural network
     */
    private fun createNetwork(): Network {
        val network = Network()

        network.addInputLayer(8)
        network.addHiddenLayer(ReLuNeuron::class, 10, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    /**
     * Play one game
     * @param network neural network
     *
     * @return fitness
     */
    private fun playGame(network: Network): Int {
        return Eater().play(network, MAX_FITNESS)
    }

    /**
     * Load network from file and test it in GUI.
     */
    private fun test() {
        val network = Network()
        network.loadTrainedNetworkFromFile()
        Eater().play(network, MAX_FITNESS, useGUI = true)
    }
}