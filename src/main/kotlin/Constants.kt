/**
 * Place for compile time constant.
 */
object Constants {
    const val MAX_NEURAL_NETWORKS = 1000
    const val MAX_GENERATIONS = 10000000
    const val NUMBER_OF_THREADS_FOR_TRAINING = 10

    const val MUTATION_PERCENT_CHANCE = 5
    const val MUTATION_RANGE_FROM = 0.95
    const val MUTATION_RANGE_TO = 1.05

    const val MAX_FITNESS = 10000000

    const val BEST_NETWORK_FILE = "best.txt"
    const val LOAD_NETWORK_FILE_ON_START = true
}