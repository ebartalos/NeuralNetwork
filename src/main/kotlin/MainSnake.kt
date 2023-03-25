import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import eater.ConsoleEater
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


object MainSnake {

    //
    private const val TRAIN = false

    @JvmStatic
    fun main(args: Array<String>) {
        if (TRAIN) {
            val networks = arrayListOf<Network>()
            val fitness = hashMapOf<Network, Int>()

            createNetworks(networks, fitness)
            train(networks, fitness)
        } else {
            replayBestGame("bestSnakeTest.txt")
        }
    }

    private fun train(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0

        for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                val fitnessOfNetwork = playConsole(network)
                if (fitnessOfNetwork >= Constants.MAX_FITNESS) break
                fitness[network] = fitnessOfNetwork
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
                playConsole(bestNetwork, saveToFile = true)
            }

            val time = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            println("$time Gen $generation best gen fitness ${fitness[bestNetwork]!!} ATH fitness $bestFitness")

            if (bestFitness >= Constants.MAX_FITNESS) {
                println("TRAINING FINISHED! SCORE IS $bestFitness")
                return
            }
        }
    }

    private fun createNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
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

    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(8)
        network.addHiddenLayer(ReLuNeuron::class, 10, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    private fun playConsole(network: Network, printBoard: Boolean = false, saveToFile: Boolean = false): Int {
        var fitness = 0
        for (i in 1..5) {
            val snake = ConsoleEater()
            val score = snake.play(network, printBoard, saveToFile)
            if (score >= Constants.MAX_FITNESS) return score
            fitness += score
        }
        return fitness
    }


    private fun replayBestGame(filename: String) {
        val scanner = Scanner(File(filename))

        while (scanner.hasNextLine()) {
            for (i in 0 until 15) {
                println(scanner.nextLine())
            }
            Thread.sleep(1_000)
        }
    }
}