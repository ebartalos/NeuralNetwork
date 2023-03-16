import ai.Network
import ai.neurons.ReLuNeuron
import ai.neurons.TanhNeuron
import snake.Snake

object MainSnake {
    @JvmStatic
    fun main(args: Array<String>) {
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()

        createNetworks(networks, fitness)

        for (network in networks) {
            startGame(network, fitness)
        }

        fitness.forEach { (network, score) ->
            println("${network.id}: $score")
        }
    }

    private fun createNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>){
        for (networkId in 1..Constants.MAX_NEURAL_NETWORKS) {
            val network = createNetwork(networkId)
            networks.add(network)
            fitness[network] = 0
        }
    }

    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(5)
        network.addHiddenLayer(ReLuNeuron::class, 18, true)
        network.addHiddenLayer(ReLuNeuron::class, 22, true)
        network.addOutputLayer(TanhNeuron::class, 4)
        network.createConnections()

        return network
    }

    private fun startGame(network: Network, fitness: HashMap<Network, Int>) {
        val snake = Snake(network)
        snake.isVisible = true

        while (snake.board.isGameOver.not()) {
            snake.changeDirection()
            Thread.sleep(snake.board.delay.toLong())
        }
        fitness[network] = snake.board.snakeBodyLength
    }
}