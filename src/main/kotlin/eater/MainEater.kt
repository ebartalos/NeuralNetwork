package eater

import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import eater.EaterConstants.MAX_FITNESS
import kotlinx.coroutines.*
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Semaphore
import kotlin.reflect.KClass


object MainEater {

    /**
     * TRAIN - trains network
     * TEST - load weights from file and test network in GUI
     * PRUNING_TEST - load weights from file and test performance after pruning
     */
    enum class Activity {
        TRAIN, TEST, PRUNING_TEST
    }

    //    private val activity: Activity = Activity.TRAIN
    private val activity: Activity = Activity.PRUNING_TEST
    private const val playgroundSize = 15

    @JvmStatic
    fun main(args: Array<String>) {
        when (activity) {
            Activity.TRAIN -> train()
            Activity.TEST -> test()
            Activity.PRUNING_TEST -> Tests.pruningTest(playgroundSize)
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

        val currentGeneration = if (EaterConstants.LOAD_NETWORK_FILE_ON_START) {
            loadGeneration()
        } else {
            0
        }

        for (generation in currentGeneration..EaterConstants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            trainInParallel(networks, fitness)

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true)

            if (fitness[bestNetwork]!! >= bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(generation = generation)
            }

            if (generation % 10 == 0) {
                val time = DateTimeFormatter
                    .ofPattern("HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.now())
                println(
                    "$time Gen ${generation.commaBetweenEveryThreeDigitsFormatter()} best gen fitness ${
                        fitness[bestNetwork]!!.commaBetweenEveryThreeDigitsFormatter()
                    } ATH fitness ${bestFitness.commaBetweenEveryThreeDigitsFormatter()}"
                )
            }

            if (bestFitness >= MAX_FITNESS) {
                println("TRAINING FINISHED! SCORE IS $bestFitness")
                return
            }
        }
    }

    private fun loadGeneration(file: File = File(EaterConstants.BEST_NETWORK_FILE)): Int {
        return file.readLines()[0].split(":")[1].toInt()
    }

    private fun Int.commaBetweenEveryThreeDigitsFormatter(): String {
        val s = StringBuilder(this.toString().reversed())
        s.forEachIndexed { index, _ ->
            if ((index % 4 == 0)) {
                s.insert(index, ",")
            }
        }
        return s.reversed().toString().dropLast(1)
    }

    /**
     * Train networks in parallel using coroutines
     *
     * @param networks all networks
     * @param fitness all networks' fitness
     */
    private fun trainInParallel(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        runBlocking {
            networks.map { network ->
                async(Dispatchers.Default) {
                    fitness[network] = playGame(network)
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
        for (networkId in 1..EaterConstants.MAX_NEURAL_NETWORKS) {
            lateinit var network: Network

            if (EaterConstants.LOAD_NETWORK_FILE_ON_START) {
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

    private fun <T : Any, U : Any> createNetwork(
        inputLayerNeurons: Int,
        hiddenLayers: ArrayList<Triple<KClass<T>, Int, Boolean>>,
        outputLayer: Pair<KClass<U>, Int>
    ): Network {
        val network = Network()

        network.addInputLayer(inputLayerNeurons)
        hiddenLayers.forEach {
            network.addHiddenLayer(it.first, it.second, it.third)
        }
        network.addOutputLayer(outputLayer.first, outputLayer.second)

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
        return Game(Eater(network), playgroundSize).play(MAX_FITNESS, useGUI = false)
    }

    /**
     * Load network from file and test it in GUI.
     */
    private fun test() {
        val network = Network()
        network.loadTrainedNetworkFromFile()

        Game((Eater(network)), playgroundSize).play(MAX_FITNESS, useGUI = true)
    }
}