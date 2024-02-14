import kotlin.random.Random

/**
 * Place for compile time constant.
 */
object Constants {
    const val MAX_NEURAL_NETWORKS = 10000
    const val MAX_GENERATIONS = 10000000
    const val NUMBER_OF_THREADS_FOR_TRAINING = 10

    const val MUTATION_PERCENT_CHANCE = 5
    val MUTATION_RANGE_FROM = Random.nextDouble()
    val MUTATION_RANGE_TO = 1 + Random.nextDouble()

    const val MAX_FITNESS = 2000000

    const val BEST_NETWORK_FILE = "src/main/kotlin/eater/trained.txt"
    const val LOAD_NETWORK_FILE_ON_START = true

    const val GUI_DOT_SIZE = 25
}