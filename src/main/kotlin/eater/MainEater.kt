package eater

import Constants
import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


object MainEater {

    /**
     * if true, train
     * if false, test
     */
    enum class Activity {
        TRAIN, REPLAY, TEST
    }

    private val activity: Activity = Activity.REPLAY

    @JvmStatic
    fun main(args: Array<String>) {
        if (activity == Activity.TRAIN) {
            val networks = arrayListOf<Network>()
            val fitness = hashMapOf<Network, Int>()

            setNetworks(networks, fitness)
            train(networks, fitness)
        } else if (activity == Activity.REPLAY) {
            replayGame("bestSnakeTest.txt")
        } else if (activity == Activity.TEST) {
            val network = Network(1)
            network.loadTrainedNetworkFromFile()
            val eater = ConsoleEater()
            val score = eater.play(network, 2000000, printBoard = false, saveToFile = true)
            println(score)
        }
    }

    /**
     * Train networks until max fitness (or max generatio limit) is reached.
     *
     * @param networks neural networks
     * @param fitness neural networks' fitness
     */
    private fun train(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0
        val maxFitness = 2000000

        for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                val fitnessOfNetwork = playGames(network, maxFitness)
                fitness[network] = fitnessOfNetwork
                if (fitnessOfNetwork >= maxFitness) break
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
                playGames(bestNetwork, maxFitness, saveToFile = true)
            }

            if (fitness[bestNetwork]!! == 0) {
                println("pica")
            }

            val time = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            println("$time Gen $generation best gen fitness ${fitness[bestNetwork]!!} ATH fitness $bestFitness")

            if (bestFitness >= maxFitness) {
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
     * Neural networks plays 5 games and total score is fitness.
     * Playing 5 games assures some statistical variance.
     *
     * @param network neural network
     * @param maxFitness upper limit for training
     * @param printBoard should be printed to console
     * @param saveToFile should be saved to file
     *
     * @return fitness
     */
    private fun playGames(
        network: Network,
        maxFitness: Int,
        printBoard: Boolean = false,
        saveToFile: Boolean = false
    ): Int {
        var fitness = 0
        for (i in 1..5) {
            val eater = ConsoleEater()
            val score = eater.play(network, maxFitness, printBoard, saveToFile)
            if (score >= maxFitness) return score
            fitness += score
        }
        return fitness
    }

    /**
     * Replays already saved game in the console.
     *
     * @param filename file containing game notation
     */
    private fun replayGame(filename: String) {
        val scanner = Scanner(File(filename))

        while (scanner.hasNextLine()) {
            for (i in 0 until 15) {
                println(scanner.nextLine())
            }
            Thread.sleep(500)
        }
    }
}