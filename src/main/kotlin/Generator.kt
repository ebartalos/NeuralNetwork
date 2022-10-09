/**
 * Generate numbers and remove them after one use.
 * Zero is excluded.
 *
 * @param from starting number
 * @param to ending number
 */
class Generator(from: Int, to: Int) {
    // all possible values
    private var values = mutableListOf<String>()

    // current value pointer
    private lateinit var current: String

    init {
        for (number in from..to) {
            // remove 0, because it's not needed for tic-tac-toe
            if (!number.toString().contains("0")) {
                values.add(number.toString())
            }
        }
    }

    /**
     * Yields random number from saved values and removes previous one from the generator
     */
    fun yield(level: Int): Int {
        if (level == 0) {
            if (::current.isInitialized) values.remove(current)
            current = values.random()
        }
        return Integer.parseInt(current[level].toString())
    }

    /**
     * @return true if all values were yielded and generator is empty.
     *         false if at least one value is still in generator.
     */
    fun isDrained(): Boolean {
        return values.size <= 1
    }
}