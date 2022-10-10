import ai.Network
import ai.algorithms.Genetics
import ai.neurons.ReLuNeuron
import ai.neurons.TanhNeuron
import java.io.File

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
//        ga()
        loadFromFileAndTestWinner()
    }

    private fun ga() {
        val networks = arrayListOf<Network>()

        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0
        val bestPossibleFitness = 6561 // generator max size - OPPONENT_PLAYER_INDEX 2
//        val bestPossibleFitness: Int = 59049 // generator max size - OPPONENT_PLAYER_INDEX 1

        for (i in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = Network(id = i)
            network.addInputLayer(9)
            network.addHiddenLayer(ReLuNeuron::class, 16, true)
            network.addHiddenLayer(ReLuNeuron::class, 20, true)
            network.addHiddenLayer(ReLuNeuron::class, 24, true)
            network.addOutputLayer(TanhNeuron::class, 9)

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
                playWithWinner(
                    bestNetwork,
                    generation,
                    bestFitness
                )
                println("Generation $generation")
                println("Best network fitness ${fitness[bestNetwork]!!}\n")
                bestFitness = fitness[bestNetwork]!!
            }

            if (generation % 100 == 0) println("Generation $generation, fitness: ${fitness[bestNetwork]!!}\n")

            if (bestFitness >= bestPossibleFitness) {
                bestNetwork.saveWeightsToFile(overwrite = true)
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
            tictactoe.play(network, playerInput = Tictactoe.PlayerInputs.HUMAN)
        }
    }

    private fun playWithWinner(
        network: Network,
        generation: Int,
        fitness: Int
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
        generationLogFile.renameTo(File("${Constants.LOG_DIRECTORY}/${Constants.AI_PLAYER_INDEX}-$generation-${match[0]}-${match[1]}-${match[2]}--${fitness}%.txt"))
    }

    private fun emptyLogsDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
}