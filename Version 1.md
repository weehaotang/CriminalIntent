**Fragment练习：使用Fragment完成应用CriminalIntent的用户界面**

![WechatIMG160](/Users/weehaotang/Downloads/WechatIMG160.jpeg)

`Crime`实例代表某种办公室陋习。一个crime有一个title、一个标识ID、一个日期和一个表示陋习是否被解决的布尔值。标题是一个描述性名称，标识ID是识别`Crime`实例的唯一元素。目前只使用一个`Crime`实例。它会被存放在`CrimeFragment`类的成员变量（`crime`）中。

`MainActivity`视图由`FrameLayout`部件组成，`FrameLayout`部件为`CrimeFragment`视图安排了显示位置。

`CrimeFragment`视图由一个`LinearLayout`部件及其三个子视图组成。这三个子视图包括一个`EditText`部件、一个`Button`部件和一个`CheckBox`部件。`CrimeFragment`类中有存储它们的属性，并设有监听器，会在响应用户操作时，更新模型层数据。



1. 创建`Crime`数据类

```kotlin
package com.bignerdranch.android.criminalintent

import java.util.*


data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
)
```

使用UUID：https://developer.android.com/reference/java/util/UUID

2. 创建UI fragment

res/values/strings.xml

```xml
<resources>
    <string name="app_name">CriminalIntent</string>
    <string name="crime_title_hint">Enter a title for the crime.</string>
    <string name="crime_title_label">Title</string>
    <string name="crime_details_label">Details</string>
    <string name="crime_solved_label">Solved</string>
</resources>
```

`CrimeFragment`的视图布局包含一个垂直`LinearLayout`部件，这个部件又含有五个子部件：两个`TextView`、一个`EditText`、一个`Button`和一个`CheckBox`。

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:orientation="vertical" android:layout_width="match_parent" android:layout_height="match_parent"
              android:layout_margin="16dp">

    <TextView
            style="?android:listSeparatorTextViewStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/crime_title_label"/>

    <EditText
            android:id="@+id/crime_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/crime_title_hint"/>

    <TextView
            style="?android:listSeparatorTextViewStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/crime_details_label"/>

    <Button
            android:id="@+id/crime_date"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            tools:text="Mon Sep 20 11:59 GMT 2021"/>

    <CheckBox
            android:id="@+id/crime_solved"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/crime_solved_label"/>

</LinearLayout>
```

创建`CrimeFragment`类

导入Jetpack库版Fragment

![截屏2021-09-21 03.28.08](/Users/weehaotang/Library/Application Support/typora-user-images/截屏2021-09-21 03.28.08.png)

实现fragment生命周期函数，新增一个`Crime`实例属性以及一个`Fragment.onCreate(Bundle?)`实现函数：

```kotlin
    //实例化fragment视图的布局，然后将实例化的`View`返回给托管activity。
    // `LayoutInflater`及`ViewGroup`是实例化布局的必要参数。
    // `Bundle`用来存储恢复数据，可供该函数从保存状态下重建视图。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }
```

（1）`Fragment.onCreate(Bundle?)`是公共函数，而`Activity.onCreate(Bundle?)`是受保护函数。Fragment.onCreate(Bundle?)`函数及其他`Fragment`生命周期函数必须是公共函数，因为托管fragment的activity要调用它们。

（2）类似于activity，fragment同样具有保存及获取状态的bundle。如同使用`Activity.onSaveInstanceState(Bundle)`函数那样，你也可以根据需要覆盖`Fragment.onSaveInstanceState(Bundle)`函数。

最后，fragment的视图并没有在`Fragment.onCreate(Bundle?)`函数中生成。创建和配置fragment视图是另一个`Fragment`生命周期函数完成的：`onCreateView(LayoutInflater, ViewGroup?, Bundle?)`。

覆盖`onCreateView()`函数

```kotlin
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

        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view
    }
```

在fragment中实例化部件：生成fragment中的`EditText`、`CheckBox`和`Button`部件。它们也是在`onCreateView()`函数里实例化。

在`onStart()`生命周期回调里给`EditText`部件添加监听器：

fragment中监听器函数的设置和activity中完全一样。创建实现`TextWatcher`监听器接口的匿名内部类。

在`onTextChanged()`函数中，调用`CharSequence`的`toString()`函数。该函数最后返回用来设置`Crime`标题的字符串。

`TextWatcher`监听器是设置在`onStart()`里的。有些监听器不仅能在用户与之交互时触发，也能在因设备旋转，视图恢复后导致数据重置时触发。能响应数据输入的监听器有`EditText`的`TextWatcher`以及`CheckBox`的`OnCheckChangedListener`。而`OnClickListener`只能响应用户交互。

视图状态在`onCreateView()`之后和`onStart()`之前恢复。视图状态一恢复，`EditText`的内容就要用`crime.title`的当前值重置。这时，如果有针对`EditText`的监听器（比如在`onCreate()`或`onCreateView()`当中），那么`TextWatcher`的`beforeTextChanged()`、`onTextChanged()`和 `afterTextChanged()`函数就会执行。在`onStart()`里设置监听器可以避免这种情况的发生，因为视图状态恢复后才会触发监听器事件。

```kotlin
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
```

向FragmentManager中添加UI fragment:

添加一个`CrimeFragment`

```kotlin
package com.bignerdranch.android.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //首先，使用R.id.fragment_container的容器视图资源ID，向FragmentManager请求并获取fragment。
    //如果要获取的fragment在队列中，FragmentManager就直接返回它。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        //创建一个新的fragment事务，执行一个fragment添加操作，然后提交该事务
        if (currentFragment == null) {
            val fragment = CrimeFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}

```

![截屏2021-09-21 04.03.33](/Users/weehaotang/Library/Application Support/typora-user-images/截屏2021-09-21 04.03.33.png)

