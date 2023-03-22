package com.example.tableapp.table

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tableapp.R
import com.example.tableapp.databinding.FragmentTableBinding

class TableFragment: Fragment(R.layout.fragment_table) {
    private var _binding: FragmentTableBinding? = null
    val binding get() = checkNotNull(_binding)
    val viewModel: TableFragmentViewModel by viewModels()
    val scoreViewList: MutableList<MutableList<EditText>> = mutableListOf()
    val sumViewList: MutableList<TextView> = mutableListOf()
    val positionViewList: MutableList<TextView> = mutableListOf()

    private fun dpToPx(dp: Int): Int {
        val scale = requireContext().resources.displayMetrics.density;
        val pixels = (dp * scale + 0.5f).toInt();
        return pixels
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTableBinding.inflate(inflater, container, false)

        createHeaderRow()
        for (i in 0 until viewModel.participantsAmount) {
            createRow(i)
        }
        initializeCells()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scoreViewList.clear()
        sumViewList.clear()
        positionViewList.clear()
    }

    private fun createTextViewCell(weight: Float = 1f) =
        TextView(context).apply {
            layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                weight).apply {
                    setBackgroundResource(R.drawable.table_cell_shape)
                    setPadding(dpToPx(3))
                    setTextAppearance(R.style.tableCellText)
                }
        }

    private fun createEditTextCell(rowIndex: Int, columnIndex: Int, weight: Float = 1f,) =
        EditText(context).apply {
            layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                weight).apply {
                setBackgroundResource(R.drawable.table_cell_shape)
                setPadding(dpToPx(3))
                setMargins(0, -dpToPx(12), 0, 0)
                imeOptions= EditorInfo.IME_ACTION_NEXT
                inputType=InputType.TYPE_CLASS_NUMBER
                setTextAppearance(R.style.tableCellText)
            }
            setOnEditorActionListener(object: OnEditorActionListener {
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    when(actionId) {
                        EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                            return validateInput(v, rowIndex, columnIndex)
                        }
                    }
                    return false
                }
            })

            setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    validateInput(view as TextView, rowIndex, columnIndex)
                }
            }
        }

    private fun createHeaderRow(weight: Float = 1f) {
        binding.table.apply {
            addView(TableRow(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    weight
                )
                addView(createTextViewCell(3f))
                addView(createTextViewCell())
                for (i in 1..viewModel.participantsAmount) {
                    addView(createTextViewCell().apply { text = i.toString() })
                }
                addView(createTextViewCell(4f).apply { text="Сумма очков" })
                addView(createTextViewCell(4f).apply { text="Место" })
            })
        }
    }

    private fun createRow(rowIndex: Int, weight: Float = 3f) {
        val rowNumber = rowIndex + 1
        binding.table.apply {
            val scoreRow = mutableListOf<EditText>()
            addView(TableRow(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                   0,
                    weight
                )
                addView(createTextViewCell(3f).apply {
                    text="Участник\n$rowNumber"
                    setLines(2)
                })
                addView(createTextViewCell().apply {
                    text=rowNumber.toString()
                })
                for (i in 1 until rowNumber) {
                    val editText = createEditTextCell(rowIndex, i - 1).apply {
                        setSingleLine()
                        hint="X"
                    }
                    addView(editText)
                    scoreRow.add(editText)
                }
                addView(createTextViewCell().apply {
                    setBackgroundColor(resources.getColor(R.color.black, activity?.theme))
                })
                for (i in rowNumber + 1..viewModel.participantsAmount) {
                    val editText = createEditTextCell(rowIndex, i - 1).apply {
                        setSingleLine()
                        hint="X"
                    }
                    addView(editText)
                    scoreRow.add(editText)
                }
                val sumView = createTextViewCell(4f)
                sumViewList.add(sumView)
                addView(sumView)
                val positionView = createTextViewCell(4f)
                positionViewList.add(positionView)
                addView(positionView)
                scoreViewList.add(scoreRow)
            })
        }
    }

    private fun validateInput(v: TextView?, rowIndex: Int, columnIndex: Int): Boolean {
        return try {
            val number = v!!.text.toString().toInt()

            Log.d("CAT", number.toString())

            with(viewModel) {
                if (number in 0..5) {
                    Log.d("CAT_TRUE", number.toString())
                    if (filledCells[rowIndex][columnIndex].not()) {
                        totalFilledCellsAmount++
                        filledCells[rowIndex][columnIndex] = true
                    }
                    scorePerRow[rowIndex][columnIndex] = number
                    if (isRowFullFilled(rowIndex)) {
                        val rowSum = scorePerRow[rowIndex].sum()
                        sumViewList[rowIndex].text = rowSum.toString()
                        Log.d("CAT_IS_FULL", "$totalFilledCellsAmount, $totalCellsAmount")
                        if (isTableFullFilled) {
                            val positions = getPositionNumbers()
                            for (i in positions.indices) {
                                positionViewList[positions[i]].text = (i + 1).toString()
                            }
                        }
                    }
                    else {
                        sumViewList[rowIndex].text = ""
                    }
                    false
                } else {
                    if (filledCells[rowIndex][columnIndex]) {
                        totalFilledCellsAmount--
                        filledCells[rowIndex][columnIndex] = false
                        scorePerRow[rowIndex][columnIndex] = 0
                        sumViewList[rowIndex].text = ""
                        for (textView in positionViewList) {
                            textView.text = ""
                        }
                    }
                    Log.d("CAT_FALSE", number.toString())
                    Toast.makeText(context, v.text, Toast.LENGTH_SHORT).show()
                    v.text = ""
                    true
                }
            }
        } catch (e: Exception) {
            Log.d("CAT_ERROR", e.message.toString())
            sumViewList[rowIndex].text = ""
            for (textView in positionViewList) {
                textView.text = ""
            }
            true
        }
    }

    private fun initializeCells() {
        with(viewModel) {
            for (rowIndex in scorePerRow.indices) {
                for (columnIndex in 0 until participantsAmount) {
                    val text = if (filledCells[rowIndex][columnIndex]) scorePerRow[rowIndex][columnIndex].toString() else ""
                    if (rowIndex > columnIndex) {
                        scoreViewList[rowIndex][columnIndex].setText(text)
                    }
                    else if (rowIndex < columnIndex) {
                        scoreViewList[rowIndex][columnIndex - 1].setText(text)
                    }
                }
            }
            for (index in sumViewList.indices) {
                if(isRowFullFilled(index)) {
                    sumViewList[index].text = scorePerRow[index].sum().toString()
                }
            }
            if (isTableFullFilled) {
                val positions = getPositionNumbers()
                for (index in positionViewList.indices) {
                    positionViewList[index].text = (positions[index] + 1).toString()
                }
            }
        }
    }
}