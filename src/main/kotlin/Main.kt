import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron
import java.io.File

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        ga()
    }

    private fun ga() {
        val networks = arrayListOf<Network>()

        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0
        val bestPossibleFitness: Int = 6561 // generator max size

        for (i in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = Network(
                inputNeurons = 9, outputNeurons = 9, id = i
            )
            network.addHiddenLayer(ReLuNeuron::class, 18, true)
            network.addHiddenLayer(ReLuNeuron::class, 22, true)
            network.addHiddenLayer(ReLuNeuron::class, 26, true)

            network.createConnections()

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
                val generator = Generator(from = 1111, to = 9999)
                iterationLoop@ for (game in 1..Constants.MAX_ITERATIONS_IN_GENERATION) {
                    val playerInput = Tictactoe.PlayerInputs.GENERATOR
                    val result = tictactoe.play(network, playerInput, generator = generator)

                    if ((result == Constants.AI_PLAYER_INDEX) || (result == 0)) {
                        fitness[network] = fitness[network]!! + 1
                    }

                    if (generator.isDrained()) {
                        break@iterationLoop
                    }
                }
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                playWithWinner(bestNetwork, generation)
                println("Generation $generation")
                println("Best network fitness ${fitness[bestNetwork]!!}\n")
                bestFitness = fitness[bestNetwork]!!
            }

            if (generation % 100 == 0) println("Generation $generation, fitness: ${fitness[bestNetwork]!!}\n")

            if (bestFitness >= bestPossibleFitness) {
                bestNetwork.saveWeightsToFile()
                return
            }
        }
    }

    private fun playWithWinner(
        network: Network,
        generation: Int
    ) {
        val match = arrayOf(0, 0, 0)
        val tictactoe = Tictactoe()

        val generationLogFile = File("${Constants.LOG_DIRECTORY}/${generation}")
        generationLogFile.writeText("") // delete content of file if exists

        for (i in 1..100) {
            val result = tictactoe.play(
                network, playerInput = Tictactoe.PlayerInputs.RANDOM, file = generationLogFile
            )
            if (result == Constants.AI_PLAYER_INDEX) { // ai won
                match[0] += 1
            } else if (result == 0) { // draw
                match[1] += 1
            } else { // random won
                match[2] += 1
            }
        }
        println("AI won: ${match[0]}")
        println("Draw: ${match[1]}")
        println("Random won: ${match[2]}")

        network.saveWeightsToFile(generationLogFile)
        generationLogFile.renameTo(File("${Constants.LOG_DIRECTORY}/${Constants.AI_PLAYER_INDEX}-$generation-${match[0]}-${match[1]}-${match[2]}.txt"))
    }

    private fun emptyLogsDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
}