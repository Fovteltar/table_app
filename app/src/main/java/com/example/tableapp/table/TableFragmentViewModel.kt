package com.example.tableapp.table

import android.util.Log
import androidx.lifecycle.ViewModel

class TableFragmentViewModel: ViewModel() {
    var participantsAmount: Int = 15
    var filledCells: MutableList<MutableList<Boolean>> = mutableListOf()
    init {
        createFilledCells()
    }
    var totalFilledCellsAmount: Int = 0
    val totalCellsAmount get() = participantsAmount * (participantsAmount - 1)
    var scorePerRow: MutableList<MutableList<Int>> = mutableListOf()
    init {
        createScorePerRow()
    }
    val isTableFullFilled get() = totalFilledCellsAmount == totalCellsAmount
    fun createFilledCells() {
        filledCells = MutableList(participantsAmount) { MutableList(participantsAmount) { false } }
        for (i in 0 until participantsAmount) {
            filledCells[i][i] = true
        }
    }
    fun createScorePerRow() {
        scorePerRow = MutableList(participantsAmount) { MutableList(participantsAmount) { 0 } }
    }
    fun isRowFullFilled(rowIndex: Int): Boolean {
        filledCells[rowIndex].forEach {
            if (it.not()) {
                return false
            }
        }
        return true
    }

    fun getPositionNumbers(): List<Int> {
        val positions = MutableList(participantsAmount) { 0 }
        val sumsWithPosition = mutableListOf<Pair<Int, Int>>()
        for (rowIndex in scorePerRow.indices) {
            sumsWithPosition.add(Pair(rowIndex, scorePerRow[rowIndex].sum()))
        }
        sumsWithPosition.sortByDescending {
            it.second
        }
        for (index in sumsWithPosition.indices) {
            positions[index] = sumsWithPosition[index].first
            Log.d("CAT", "$index, ${positions[index]} ${sumsWithPosition[index].second}")
        }
        return positions
    }
}