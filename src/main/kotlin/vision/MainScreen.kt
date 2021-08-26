package vision

import model.Board
import model.EventBoard
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.JOptionPane

fun main(args: Array<String>) {
    MainScreen()
}

class MainScreen : JFrame() {
    private val board = Board(amountOfRows = 16, amountOfColumns = 30, amountOfMines = 89)
    private val boardPanel = BoardPanel(board)

    init {
        board.onEvent(this::showResult)
        add(boardPanel)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Minesweeper"
        isVisible = true
    }

    private fun showResult(event: EventBoard) {
        SwingUtilities.invokeLater {
            val msg = when(event) {
                EventBoard.VICTORY -> "YOU WON THE GAME... :)"
                EventBoard.DEFEAT -> "YOU LOST THE GAME... :P"
            }

            JOptionPane.showMessageDialog(this, msg)
            board.reset()

            boardPanel.repaint()
            boardPanel.validate()
        }
    }
}