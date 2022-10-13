import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron
import ai.neurons.SigmoidNeuron
import java.io.File

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        ga()
//        loadFromFileAndTestWinner()
    }

    private fun ga() {
        val networks = arrayListOf<Network>()

        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0
        val bestPossibleFitness = 13122 // 6561 * 2

        var aiPlayerIndex: Int

        for (i in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = Network(id = i)

            if (Constants.LOAD_NETWORK_FILE_ON_START) {
                network.loadTrainedNetworkFromFile()
            } else {
                network.addInputLayer(9)
                network.addHiddenLayer(ReLuNeuron::class, 18, true)
                network.addHiddenLayer(ReLuNeuron::class, 22, true)
                network.addHiddenLayer(ReLuNeuron::class, 26, true)
                network.addOutputLayer(SigmoidNeuron::class, 9)

                network.createConnections()
            }

            networks.add(network)
            fitness[network] = 0
        }

        val directory = File(Constants.LOG_DIRECTORY)
        emptyLogsDirectory(directory)

        generationLoop@ for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                val tictactoe = Tictactoe()
                val generator1 = Generator(from = 1111, to = 9999)
                aiPlayerIndex = 1
                iterationLoop1@ for (game in 1..Constants.MAX_ITERATIONS_IN_GENERATION) {
                    val playerInput = Tictactoe.PlayerInputs.GENERATOR
                    val result = tictactoe.play(network, aiPlayerIndex, playerInput, generator = generator1)

                    if ((result == aiPlayerIndex) || (result == 0)) {
                        fitness[network] = fitness[network]!! + 1
                    }

                    if (generator1.isDrained()) {
                        break@iterationLoop1
                    }
                }


                val generator2 = Generator(from = 1111, to = 9999)
                aiPlayerIndex = 2

                iterationLoop2@ for (game in 1..Constants.MAX_ITERATIONS_IN_GENERATION) {
                    val playerInput = Tictactoe.PlayerInputs.GENERATOR
                    val result = tictactoe.play(network, aiPlayerIndex, playerInput, generator = generator2)

                    if ((result == aiPlayerIndex) || (result == 0)) {
                        fitness[network] = fitness[network]!! + 1
                    }

                    if (generator2.isDrained()) {
                        break@iterationLoop2
                    }
                }
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                println("Generation $generation")
                println("Best network fitness ${fitness[bestNetwork]!!}")

                bestFitness = fitness[bestNetwork]!!

                playWithWinner(bestNetwork, generation, bestFitness, 1)
                playWithWinner(bestNetwork, generation, bestFitness, 2)
            }

            if (generation % 50 == 0) {
                println("\nGeneration $generation, fitness: ${fitness[bestNetwork]!!}\n")
            }

            if (bestFitness >= bestPossibleFitness) {
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
                return
            }
        }
    }

    private fun loadFromFileAndTestWinner() {
        val network = Network(
            id = 1
        )

        network.loadTrainedNetworkFromFile()

        for (i in 0..20) {
            val tictactoe = Tictactoe()
            tictactoe.play(network, (1..2).random(), playerInput = Tictactoe.PlayerInputs.HUMAN)
        }
    }

    private fun playWithWinner(
        network: Network,
        generation: Int,
        fitness: Int,
        aiPlayerIndex: Int
    ) {
        val match = arrayOf(0, 0, 0)
        val tictactoe = Tictactoe()

        val generationLogFile = File("${Constants.LOG_DIRECTORY}/${generation}")
        generationLogFile.writeText("") // delete content of file if exists

        for (i in 1..100) {
            val result = tictactoe.play(
                network,
                aiPlayerIndex = aiPlayerIndex,
                playerInput = Tictactoe.PlayerInputs.RANDOM,
                file = generationLogFile
            )
            if (result == aiPlayerIndex) { // ai won
                match[0] += 1
            } else if (result == 0) { // draw
                match[1] += 1
            } else { // random won
                match[2] += 1
            }
        }
        println("AI won: ${match[0]}")
        println("Draw: ${match[1]}")
        println("Random won: ${match[2]}\n")

        network.saveTrainedNetworkToFile(generationLogFile)
        generationLogFile.renameTo(File("${Constants.LOG_DIRECTORY}/${aiPlayerIndex}--$generation--${match[0]}-${match[1]}-${match[2]}--${fitness}.txt"))

        network.saveTrainedNetworkToFile(overwrite = true)
    }

    private fun emptyLogsDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
}