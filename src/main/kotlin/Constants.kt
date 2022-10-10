/**
 * Place for compile time constant.
 */
object Constants {
    const val AI_PLAYER_INDEX = 1
    const val OPPONENT_PLAYER_INDEX = 2

    const val MAX_NEURAL_NETWORKS = 10
    const val MAX_GENERATIONS = 500000
    const val MAX_ITERATIONS_IN_GENERATION = 10000000

    const val MUTATION_CHANCE = 5
    const val MUTATION_RANGE_FROM = 0.95
    const val MUTATION_RANGE_TO = 1.05

    const val LOG_DIRECTORY = "src/logs"
    const val WEIGHTS_FILE = "weights.txt"
}