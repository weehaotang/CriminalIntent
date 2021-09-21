package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    //实例化fragment视图的布局，然后将实例化的`View`返回给托管activity。
    // `LayoutInflater`及`ViewGroup`是实例化布局的必要参数。
    // `Bundle`用来存储恢复数据，可供该函数从保存状态下重建视图。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }
    //fragment的视图是直接通过调用LayoutInflater.inflate()函数并传入布局的资源ID生成的。
    //第二个参数是视图的父视图，我们通常需要父视图来正确配置部件。
    //第三个参数告诉布局生成器是否立即将生成的视图添加给父视图。这里传入了false参数，
    //fragment的视图将由activity的容器视图托管。稍后，activity会处理
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        //禁用Button按扭
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This space intentionally left blank
            }
        }

        titleField.addTextChangedListener(titleWatcher)
        //在onCreateView()里引用CheckBox，然后在onStart()里设置监听器，根据用户操作，更新solvedCheckBox状态，
        //虽然CheckBox部件的监听器不会因fragment的状态恢复而触发，但把它放在onStart()里，更容易查找
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}