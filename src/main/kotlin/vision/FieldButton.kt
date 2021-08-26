package vision

import model.EventField
import model.Field
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private val COR_BACKGROUNG_NORMAL = Color(184, 184, 184)
private val COR_BACKGROUNG_MARKING = Color(8, 179, 247)
private val COR_BACKGROUNG_EXPLOSION = Color(189, 66, 68)
private val COR_TXT_VERDE = Color(0, 100, 0)

class FieldButton(private val field: Field) : JButton() {

    init {
        font = font.deriveFont(Font.BOLD)
        background = COR_BACKGROUNG_NORMAL
        isOpaque = true
        border = BorderFactory.createBevelBorder(0)
        addMouseListener(MouseClickListener(field, { it.open() }, { it.changeMarking() }))

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: EventField) {
        when (event) {
            EventField.EXPLOSION -> applyExplodedStyle()
            EventField.OPENING -> applyOpenStyle()
            EventField.MARKING -> applyMarkedStyle()
            else -> applyDefaultStyle()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    private fun applyExplodedStyle() {
        background = COR_BACKGROUNG_EXPLOSION
        text = "X"
    }

    private fun applyOpenStyle() {
        background = COR_BACKGROUNG_NORMAL
        border = BorderFactory.createLineBorder(Color.GRAY)

        foreground = when (field.amountOfMinedNeighbors) {
            1 -> COR_TXT_VERDE
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4, 5, 6 -> Color.RED
            else -> Color.PINK
        }

        text = if (field.amountOfMinedNeighbors > 0) field.amountOfMinedNeighbors.toString() else ""
    }

    private fun applyMarkedStyle() {
        background = COR_BACKGROUNG_MARKING
        foreground = Color.BLACK
        text = "M"
    }

    private fun applyDefaultStyle() {
        background = COR_BACKGROUNG_NORMAL
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }
}