package com.mep.rxandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.subjects.PublishSubject
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    val source = io.reactivex.subjects.PublishSubject.create<String>()
    val disposable = CompositeDisposable()
    var count = 0;
    companion object{
        private val TAG = "[RXJAVA WITH KOTLIN]"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        just.setOnClickListener {
            CreateObservableWithJust1()
            CreateObservableWithJust2()
        }

        fromArray.setOnClickListener {
            CreateObservableWithFromArray()
        }

        fromIter.setOnClickListener {
            CreateObservableWithFromIterator()
        }

        createObs.setOnClickListener {
            CreateObservableWithCreate()
        }

        interval.setOnClickListener {
            CreateObservableWithInterval()
        }

        intervalRange.setOnClickListener {
            CreateObservableWithIntervalRange()
        }

        createCustomizedEmittedType()

        map.setOnClickListener {
            AddOperatorMap()
        }

        flatmap.setOnClickListener {
            AddOperatorFlatMap()
        }

        zip.setOnClickListener {
            AddOperatorZip()
        }

       concat.setOnClickListener {
           AddOperatorConcat()
       }

        merge.setOnClickListener {
            AddOperatorMerge()
        }

        filter.setOnClickListener {
            AddOperatorFilter()
        }


        disposable.add(
            source.subscribe(
                {t -> eventText.text = t},
                {e -> eventText.text = "error $e"}
            )
        )
        emit.setOnClickListener {
            source.onNext("number $count ")
            if(count == 10)
                source.onComplete()
            else count++
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
    fun CreateObservableWithJust1() {

        Log.d(TAG, "Create Observable with Just")
        Observable.just(
                "Study",
                "Read",
                "Play",
                "Eat"
        ).subscribe { Log.d(TAG, it) }
    }

    fun CreateObservableWithJust2() {

        Log.d(TAG, "Create Observable with Just and result OnNext/OnError/OnComplete")
        Observable.just(
                "Study",
                "Read",
                "Play",
                "Eat"
        ).subscribe(
                {data -> Log.d(TAG, "OnNext - Received -$data")},
                {error -> Log.d(TAG, "OnError - error - $error")},
                {Log.d(TAG, "OnComplete - complete")}
        )
    }

    fun CreateObservableWithFromArray(){

        //val list = listOf("Study", "Read", "Play", "Eat")
        Log.d(TAG, "Create Observable from array")
        Observable.fromArray("Study", "Read", "Play", "Eat")
                .subscribe(
                        {data -> Log.d(TAG,"OnNext - Received -$data")},
                        {error -> Log.d(TAG,"OnError - error - $error")},
                        {Log.d(TAG,"OnComplete - complete")})
    }

    fun CreateObservableWithFromIterator(){

        Log.d(TAG, "Create Observable from Iterable list")
        val list = listOf("Study", "Read", "Play", "Eat")
        Observable.fromIterable(list)
                .subscribe(
                        {data -> Log.d(TAG,"OnNext - Received -$data")},
                        {error -> Log.d(TAG,"OnError - error - $error")},
                        {Log.d(TAG,"OnComplete - complete")})
    }

    // Create Observable with create function
    fun CreateObservableWithCreate(){
        Log.d(TAG, "Create Observable from create function")
        val list = listOf("Study", "Read", "Play", "Eat")
        getObservable(list).subscribe(
                {data -> Log.d(TAG,"OnNext - Received -$data")},
                {error -> Log.d(TAG,"OnError - error - $error")},
                {Log.d(TAG,"OnComplete - complete")}
        )
    }
    // fuction to make observable of type String
    fun getObservable(list : List<String>)
            = Observable.create<String>{  emitter ->
                list.forEach {
                    if(it.equals(""))
                        emitter.onError(Exception("emitter fails"))
                    else
                        emitter.onNext(it)
                }
                emitter.onComplete()
    }

    fun CreateObservableWithInterval(){
        Log.d(TAG, "Create Observable with Interval")
        Observable.interval(1000, TimeUnit.MILLISECONDS)
            .take(10)
                .subscribe{Log.d(TAG, "Received Event + $it")}
    }

    fun CreateObservableWithIntervalRange(){
        Log.d(TAG, "Create Observable with IntervalRange")
        Observable.intervalRange(100,
                                10,
                               0,
                                    1, TimeUnit.SECONDS)
                .subscribe{Log.d(TAG, "Received Event - $it")}
    }


    fun createCustomizedEmittedType(){
        Observable.just("Hello")
                .doOnSubscribe { Log.d(TAG,"Subscribed") }
                .doOnNext { s -> Log.d(TAG,"Received: $s") }
                .doAfterNext { Log.d(TAG,"After Receiving") }
                .doOnError { e -> Log.d(TAG,"Error: $e") }
                .doOnComplete { Log.d(TAG,"Complete") }
                .doFinally { Log.d(TAG,"Do Finally!") }
                .doOnDispose { Log.d(TAG,"Do on Dispose!") }
                .subscribe { Log.d(TAG,"Subscribe") }
    }

    fun AddOperatorMap(){

        Log.d(TAG, "Add Map to Observable chain")
        val list = listOf("Study", "Read", "Play", "Eat")
        Observable.fromIterable(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { t -> "$t very hard" }
            .subscribe(
                {data -> Log.d(TAG,"OnNext - Received -$data")},
                {error -> Log.d(TAG,"OnError - error - $error")},
                {Log.d(TAG,"OnComplete - complete")})

    }

    fun AddOperatorFlatMap() {

        Log.d(TAG, "Add FlatMap to Observable chain")
        val list = listOf("Study", "Read", "Play", "Eat")
        Observable.fromIterable(list)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t ->
                Observable.just(t+ " very hard")
                    .subscribeOn(Schedulers.io())}
            .subscribe(
                {data -> Log.d(TAG,"OnNext - Received -$data")},
                {error -> Log.d(TAG,"OnError - error - $error")},
                {Log.d(TAG,"OnComplete - complete")})
    }



    fun AddOperatorZip()
    {
        val ver = listOf("Study", "Read", "Play", "Eat")
        val obj = listOf("Math", "Book", "Piano", "Pizza")
        Observable.zip(

            Observable.fromIterable(ver),
            Observable.fromIterable(obj),
            BiFunction<String,  String, String> {  ver, obj ->
                " $ver  $obj"}
        )
            .subscribe {data -> Log.d(TAG,"OnNext - Received -$data")}

    }

    fun AddOperatorConcat() {

        val ver = listOf("Study", "Read", "Play", "Eat")
        val obj = listOf("Math", "Book", "Piano", "Pizza")
        Observable.concat(

            Observable.fromIterable(ver),
            Observable.fromIterable(obj)

        )
            .subscribe {data -> Log.d(TAG,"OnNext - Received -$data")}

    }

    fun AddOperatorMerge(){

        Observable.merge(
            Observable.interval(250, TimeUnit.MILLISECONDS).map { i -> "Math" },
            Observable.interval(150, TimeUnit.MILLISECONDS).map { i -> "English" })
            .take(10)
            .subscribe{ v -> Log.d(TAG, "Received: $v") }
    }

    fun AddOperatorFilter(){

        val list = listOf(100, 200, 300, 240, 137, 225)
        Observable.fromIterable(list)
            .filter {t -> t < 200}
            .subscribe{ v -> Log.d(TAG, "Received: $v") }
    }

    fun disposable(){
        val compositeDisposable = CompositeDisposable()

        val observableOne = Observable.just("Math")
            .subscribe { v -> Log.d(TAG,"Received: $v") }
        val observableTwo = Observable.just("English")
            .subscribe { v -> Log.d(TAG,"Received: $v") }

        compositeDisposable.add(observableOne)
        compositeDisposable.add(observableTwo)
        compositeDisposable.clear()
    }

}


