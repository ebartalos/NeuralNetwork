@file:Suppress("SameParameterValue")

package eater

import Constants
import Constants.MAX_FITNESS
import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import ai.neurons.SigmoidNeuron
import ai.neurons.TanhNeuron
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
     */
    enum class Activity {
        TRAIN, TEST
    }

    private val activity: Activity = Activity.TRAIN
//            private val activity: Activity = Activity.TEST
    private const val playgroundSize = 15

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

        val currentGeneration = if (Constants.LOAD_NETWORK_FILE_ON_START) {
            loadGeneration()
        } else {
            0
        }

        for (generation in currentGeneration..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            trainInParallel(networks, fitness)

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_PERCENT_CHANCE)

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

    private fun loadGeneration(file: File = File(Constants.BEST_NETWORK_FILE)): Int {
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
                network = createNetwork(
                    8,
                    arrayListOf(
                        Triple(SigmoidNeuron::class, 10, true),
                        Triple(SigmoidNeuron::class, 10, true),
                        Triple(SigmoidNeuron::class, 10, true),
                        ),
                    Pair(Neuron::class, 4)
                )
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
        return Game(arrayListOf(Eater(network)), playgroundSize).play(MAX_FITNESS, useGUI = false)
    }

    /**
     * Load network from file and test it in GUI.
     */
    private fun test() {
        val network = Network()
        network.loadTrainedNetworkFromFile()

        val eaters = arrayListOf(Eater(network))

        val game = Game(eaters, playgroundSize)
        game.play(MAX_FITNESS, useGUI = true)
    }
}