import snake.Snake
import java.awt.EventQueue

object MainSnake {
    @JvmStatic
    fun main(args: Array<String>) {
        EventQueue.invokeLater {
            val ex = Snake()
            ex.isVisible = true
        }
    }
}