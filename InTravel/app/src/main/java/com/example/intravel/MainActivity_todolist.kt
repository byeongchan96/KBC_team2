package com.example.intravel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.intravel.adapter.TodoListAdapter
import com.example.intravel.client.SubClient
import com.example.intravel.data.TodoList
import com.example.intravel.databinding.ActivityMainTodolistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity_todolist : AppCompatActivity() {

    var currentTId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainTodolistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentTId = intent.getLongExtra("T_ID", 0) // 메인 화면에서 전달된 tId

        // 데이터 및 어댑터 생성, 리사이클러뷰 연결
        val todoList = mutableListOf<TodoList>()
        val todoListAdapter = TodoListAdapter(todoList)
        binding.todoListRecyclerView.adapter = todoListAdapter
        binding.todoListRecyclerView.layoutManager = LinearLayoutManager(this)


        // 전체 todolist 보기 (바로 화면에 보여야함)
        // DB 연결용
//        SubClient.retrofit.findAllTodoList().enqueue(object :retrofit2.Callback<List<TodoList>> {
//            override fun onResponse(call: Call<List<TodoList>>, response: Response<List<TodoList>>) {
//                todoListAdapter.todoList.clear()
//                todoListAdapter.todoList = response.body() as MutableList<TodoList>
//                todoListAdapter.notifyDataSetChanged()
//            }
//            override fun onFailure(call: Call<List<TodoList>>, t: Throwable) {
//            }
//        })  //findAllTodoList


        // 추가
        // DB 연결 전 테스트용 - 추후 삭제
        binding.btnTodoListAdd.setOnClickListener {
            todoListAdapter.todoList.add(TodoList(0, currentTId, "", 'N', 'N'))
            todoListAdapter.notifyItemInserted(todoListAdapter.todoList.size)
        }   // 인덱스 0부터 시작하니까 크기는 -1
        // DB 연결용
//        binding.btnTodoListAdd.setOnClickListener {
//            val newTodo = TodoList(0, currentTId, "", 'N', 'N')
//            SubClient.retrofit.insertTodoList(newTodo).enqueue(object :retrofit2.Callback<TodoList> {
//                override fun onResponse(call: Call<TodoList>, response: Response<TodoList>) {
//                    todoListAdapter.addTodoList(response.body()!!)
//                }
//                override fun onFailure(call: Call<TodoList>, t: Throwable) {
//                }
//            })  //insertTodoList
//        }   //btnTodoListAdd
    }
}