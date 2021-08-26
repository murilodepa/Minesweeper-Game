package model

import java.util.*
import kotlin.collections.ArrayList

enum class EventBoard { VICTORY, DEFEAT }

class Board(val amountOfRows: Int, val amountOfColumns: Int, private val amountOfMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(EventBoard) -> Unit>()

    init {
        generateFields()
        associateNeighbors()
        drawMines()
    }

    private fun generateFields() {
        for (row in 0 until amountOfRows) {
            fields.add(ArrayList())
            for (column in 0 until amountOfColumns) {
                val newField = Field(row, column)
                newField.onEvent(this::checkDefeatOrVictory)
                fields[row].add(newField)
            }
        }
    }

    private fun associateNeighbors() {
        forEachField { associateNeighbors(it) }
    }

    private fun associateNeighbors(field: Field) {
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbors(it) }
            }
        }
    }

    private fun drawMines() {
        val generator = Random()
        var drawnRow = -1
        var drawnColumn = -1
        var currentAmountOfMines = 0

        while (currentAmountOfMines < this.amountOfMines) {
            drawnRow = generator.nextInt(amountOfRows)
            drawnColumn = generator.nextInt(amountOfColumns)

            val drawnField = fields[drawnRow][drawnColumn]
            if (drawnField.safe) {
                drawnField.undermine()
                currentAmountOfMines++
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachField { if (!it.goalAchieved) playerWon = false }
        return playerWon
    }

    private fun checkDefeatOrVictory(field: Field, event: EventField) {
        if (event == EventField.EXPLOSION) {
            callbacks.forEach { it(EventBoard.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(EventBoard.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { row -> row.forEach(callback) }
    }

    fun onEvent(callback: (EventBoard) -> Unit) {
        callbacks.add(callback)
    }

    fun reset() {
        forEachField { it.restart() }
        drawMines()
    }
}