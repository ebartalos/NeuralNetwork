class Generator(from: Int, to: Int) {
    // pointers to various values in values array
    private var values = mutableListOf<String>()
    private lateinit var current: String

    init {
        for (number in from..to) {
            if (!number.toString().contains("0")) {
                values.add(number.toString())
            }
        }
    }

    fun yield(level: Int): Int {
        if (level == 0) {
            if (::current.isInitialized) values.remove(current)
            current = values.random()
        }
        return Integer.parseInt(current[level].toString())
    }

    fun isDrained(): Boolean {
        return values.size <= 1
    }
}