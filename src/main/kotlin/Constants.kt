/**
 * Place for compile time constant.
 */
object Constants {
    const val MAX_NEURAL_NETWORKS = 1000
    const val MAX_GENERATIONS = 5000000
    const val MAX_ITERATIONS_IN_GENERATION = 10000000

    const val MUTATION_CHANCE = 10
    const val MUTATION_RANGE_FROM = 0.95
    const val MUTATION_RANGE_TO = 1.05

    const val LOG_DIRECTORY = "src/logs"
    const val BEST_NETWORK_FILE = "best.txt"

    const val LOAD_NETWORK_FILE_ON_START = true
}