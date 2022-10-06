class Generator(private val values: ArrayList<Int>, amount: Int) {
    // pointers to various values in values array
    var generators = ArrayList<Int>()

    // pointer to current generator for yielding
    private var activeGeneratorPointer = 0

    private var start = true

    init {
        for (i in 1..amount) {
            generators.add(0)
        }
        resetGenerator()
    }

    fun yield(): Int {
        val result = values[generators[activeGeneratorPointer]]
        if (start) {
            start = false
            return result
        }
        if (activeGeneratorPointer < generators.size - 1) {
            activeGeneratorPointer += 1
        }
        return result
    }

    fun increment() {
        checkDrainedGenerator()
        while (findCorrectIncrement()) {
        }
        generators[activeGeneratorPointer] += 1
    }

    fun resetGenerator() {
        activeGeneratorPointer = 0
    }

    private fun findCorrectIncrement(): Boolean {
        return if (values[generators[activeGeneratorPointer]] == values.last()) {
            generators[activeGeneratorPointer] = 0
            activeGeneratorPointer -= 1
            true
        } else {
            false
        }
    }

    private fun checkDrainedGenerator() {
        if (generators.all { it == (values.size - 1) }) {
            throw Exception("Generator is drained!")
        }
    }
}