import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import snake.Snake


object MainSnake {

    @JvmStatic
    fun main(args: Array<String>) {
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0

        createNetworks(networks, fitness)

        generationLoop@ for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                startGame(network, fitness, false)
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
            }
            println("Generation $generation best generation fitness ${fitness[bestNetwork]!!} best overall fitness $bestFitness")

            startGame(bestNetwork, null, true)
        }
    }

    private fun createNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        for (networkId in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = createNetwork(networkId)
            networks.add(network)
            fitness[network] = 0
        }
    }

    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(8)
        network.addHiddenLayer(ReLuNeuron::class, 9, true)
        network.addHiddenLayer(ReLuNeuron::class, 9, true)
//        network.addHiddenLayer(ReLuNeuron::class, 8, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    private fun startGame(network: Network, fitness: HashMap<Network, Int>?, goSlow: Boolean) {
        val snake = Snake(network)
        snake.isVisible = true
        if (goSlow) {
            snake.toFront()
        } else {
            snake.toBack()
        }

        while (snake.board.isGameOver.not()) {
            snake.changeDirection()
            if (goSlow) {
                Thread.sleep(10)
            }
        }
        if (fitness != null) fitness[network] = snake.fitness()
        snake.dispose()
    }
}