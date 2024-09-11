package com.example.intravel.Fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.intravel.DetailMainActivity
import com.example.intravel.MainActivity
import com.example.intravel.R
import com.example.intravel.adapter.MoneyAdapter
import com.example.intravel.client.SubClient
import com.example.intravel.data.MoneyData
import com.example.intravel.data.PayData
import com.example.intravel.databinding.CustomMoneyBinding
import com.example.intravel.databinding.FragmentMoneytabBinding
import com.example.intravel.databinding.FragmentTodoListBinding
import retrofit2.Call
import retrofit2.Response
import java.lang.Long.parseLong


class MoneyTabFragment : Fragment() {

    private lateinit var binding: FragmentMoneytabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMoneytabBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.moneyRecyclerView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        var payList = mutableListOf<PayData>()

        // 데이터 생성
        var moneyList = mutableListOf<MoneyData>()
        // 어댑터 생성
        var moneyAdapter = MoneyAdapter(requireContext(),moneyList)
        // 리사이클러뷰 어댑터 연결
        binding.moneyRecyclerView.adapter = moneyAdapter
        // 레이아웃 설정
        binding.moneyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 메인에서 보낸 travId 받아오기
        val tId = activity?.intent?.getLongExtra("tId", 0) ?: 0

        // 첫화면에 리스트 불러오기
        SubClient.retrofit.findAllMoneyList(tId).enqueue(object :retrofit2.Callback<List<MoneyData>>{
            override fun onResponse(call: Call<List<MoneyData>>, response: Response<List<MoneyData>>) {
                moneyAdapter.moneyList = response.body() as MutableList<MoneyData>
                Log.d("moneyList","${response.body()}")
                moneyAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<MoneyData>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        // 추가
        binding.btnMoneyAdd.setOnClickListener{

            var addDialog = CustomMoneyBinding.inflate(layoutInflater)
            AlertDialog.Builder(requireContext()).run{
                setTitle("예산 카테고리 추가하기")
                setView(addDialog.root)

                setPositiveButton("확인",object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        var m = MoneyData(0,
                            tId,
                            addDialog.edtTitle.text.toString(),
                            parseLong(addDialog.edtMoney.text.toString()) // long형으로 변환
                        )
                        SubClient.retrofit.insertMoney(tId,m).enqueue(object:retrofit2.Callback<MoneyData>{
                            override fun onResponse(
                                call: Call<MoneyData>, response: Response<MoneyData>
                            ) {
                                response.body()?.let { it1 -> moneyAdapter.insertMoney(it1) }
                            }

                            override fun onFailure(call: Call<MoneyData>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })// enqueue
                    } // onclick
                }) // positive

                setNegativeButton("취소",null)
                show()
            }
        } // btnMoneyAdd


        // 머니 카드뷰가 클릭되면 수정 버튼 보이고 해당 카드뷰만 수정할 수 있도록 함
        moneyAdapter.onItemClickListener = object:MoneyAdapter.OnItemClickListener{
            override fun onItemClick(money: MoneyData, position: Int) {

                // on/off
                if(binding.btnMoneyEdt.isVisible == true){
                    binding.btnMoneyEdt.isVisible = false
                }else{
                    binding.btnMoneyEdt.isVisible = true
                }

                binding.btnMoneyEdt.setOnClickListener {
                    var edtDialog = CustomMoneyBinding.inflate(LayoutInflater.from(it.context))
                    AlertDialog.Builder(it.context).run{
                        setTitle("예산 카테고리 수정하기")
                        setView(edtDialog.root)
                        edtDialog.txt1.text = "수정할 이름을 입력해주세요."
                        edtDialog.edtTitle.setText(money.moneyTitle)
                        edtDialog.txt2.text = "수정할 금액을 입력해주세요."
                        edtDialog.edtMoney.setText(money.expenses.toString())

                        setPositiveButton("확인",object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                var m = MoneyData(money.moneyId,
                                    money.travId,
                                    edtDialog.edtTitle.text.toString(),
                                    parseLong(edtDialog.edtMoney.text.toString()), // long형으로 변환
                                )
                                SubClient.retrofit.updateMoney(money.moneyId,m).enqueue(object:retrofit2.Callback<MoneyData>{
                                    override fun onResponse(call: Call<MoneyData>, response: Response<MoneyData>) {
                                        moneyAdapter.updateMoney(m,position)
                                    }

                                    override fun onFailure(call: Call<MoneyData>, t: Throwable) {
                                        TODO("Not yet implemented")
                                    }
                                }) // enqueue
                            }
                        }) // positive

                        // positive 버튼 한개 더 만들어서 삭제로 하면 위치가 똑같고 얘 쓰면 왼쪽 끝에 나옴
                        setNeutralButton("삭제",object:DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
//                        removeData(holder.adapterPosition)

                                // db 연결버전
                                SubClient.retrofit.deleteByIdMoney(money.moneyId).enqueue(object:retrofit2.Callback<Void>{
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        moneyAdapter.removeMoney(position)
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        TODO("Not yet implemented")
                                    }
                                })//enqueu
                            }//onclick
                        })//Neutral 삭제

                        setNegativeButton("취소",null)
                        show()
                    } // dialog
                }// btnMoneyEdt
            } // onItemClick
        } // ItemClickListener
        
        // 메인에서 받은 travId로 페이 목록 불러와서 총 지출 계산하면 됨
//        SubClient.retrofit.findTravIdPayList(tId).enqueue(object :retrofit2.Callback<List<PayData>>{
//            override fun onResponse(call: Call<List<PayData>>, response: Response<List<PayData>>) {
//                if(response.body())
//            }
//
//            override fun onFailure(call: Call<List<PayData>>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
        
        
        
    }

    }


