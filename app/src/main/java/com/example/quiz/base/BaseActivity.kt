package com.example.quiz.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected val binding: VB by lazy { inflateViewBinding(layoutInflater) }

    protected abstract fun inflateViewBinding(inflater: LayoutInflater): VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    protected fun <T> LiveData<T>.observe(observer: Observer<in T>) =
        observe(this@BaseActivity, observer)

}