package eater

import Constants
import Constants.MAX_FITNESS
import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Semaphore


object MainEater {

    /**
     * TRAIN - trains network
     * TEST - load weights from file and test network in GUI
     */
    enum class Activity {
        TRAIN, TEST
    }

    private val activity: Activity = Activity.TRAIN


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

            trainInParallel(networks, fitness)

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_PERCENT_CHANCE)

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
     * Train networks in parallel using coroutines
     *
     * @param networks all networks
     * @param fitness all networks' fitness
     */
    private fun trainInParallel(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        val semaphore = Semaphore(Constants.NUMBER_OF_THREADS_FOR_TRAINING)

        runBlocking {
            networks.map { network ->
                semaphore.acquire()
                async(Dispatchers.Default) {
                    fitness[network] = playGame(network)
                    semaphore.release()
                }
            }.awaitAll()
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
        network.addHiddenLayer(ReLuNeuron::class, 10, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    /**
     * Play 1 game, until eater crashes or runs out of steps.
     *
     * @param network neural network playing the game
     *
     * @return fitness reached
     */
    private fun playGame(network: Network): Int {
        val game = Game(arrayListOf(Eater(network)), 15)
        return game.play(MAX_FITNESS, useGUI = false)
    }

    /**
     * Load network from file and test it in GUI.
     */
    private fun test() {
        val network = Network()
        network.loadTrainedNetworkFromFile()

        val eaters = arrayListOf(
            Eater(network),
//            Eater(network),
//            Eater(network)
        )

        val game = Game(eaters, 15)
        game.play(MAX_FITNESS, useGUI = true)
    }
}