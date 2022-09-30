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
        val tictactoe = Tictactoe()
        val networks = arrayListOf<Network>()

        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        val successMap = hashMapOf<Int, String>()

        for (i in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = Network(
                inputNeurons = 9, outputNeurons = 9, id = i
            )
            network.addHiddenLayer(ReLuNeuron::class, 20, true)
            network.addHiddenLayer(ReLuNeuron::class, 25, true)
            network.addHiddenLayer(ReLuNeuron::class, 30, true)

            network.createConnections(true)

            networks.add(network)
            fitness[network] = 0
        }

        val directory = File(Constants.LOG_DIRECTORY)
        emptyLogsDirectory(directory)

        generationLoop@ for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                for (game in 1..50) {
                    val result = tictactoe.play(network, isPlayerSecond = true, isInputRandom = true)
                    if (result == 1) { // win
                        fitness[network] = fitness[network]!! + 2
                    } else if (result == 0) { // draw
                        fitness[network] = fitness[network]!! + 1
                    }
                }
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (generation % Constants.TEST_EACH_X_GENERATION == 0) {
                playWithWinner(tictactoe, sortedFitness, generation, successMap)
                println("Generation $generation \n")
            }
        }

        successMap.toSortedMap().forEach { (generation, score) ->
            println("$generation - $score")
        }
    }

    private fun playWithWinner(
        tictactoe: Tictactoe,
        sortedFitness: Map<Network, Int>,
        generation: Int,
        successMap: HashMap<Int, String>
    ) {
        val match = arrayOf(0, 0, 0)
        val generationLogFile = File("${Constants.LOG_DIRECTORY}/${generation}.txt")
        generationLogFile.writeText("") // delete content of file if exists
        for (i in 1..50) {
            val result = tictactoe.play(
                sortedFitness.keys.last(), isPlayerSecond = true, isInputRandom = true, file = generationLogFile
            )
            if (result == 1) { // ai won
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

        successMap[generation] = "${match[0]}, ${match[1]}, ${match[2]}"
        generationLogFile.renameTo(File("${Constants.LOG_DIRECTORY}/$generation-${match[0]}-${match[1]}-${match[2]}"))
    }

    private fun emptyLogsDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }
}