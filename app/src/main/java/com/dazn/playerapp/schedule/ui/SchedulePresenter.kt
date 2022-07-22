package com.dazn.playerapp.schedule.ui

import com.dazn.playerapp.api.PlayerService
import com.dazn.playerapp.di.DaggerApiComponent
import com.dazn.playerapp.events.domain.EventsDataUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class SchedulePresenter(private var view: ScheduleContract.View?) : ScheduleContract.Presenter {

    @Inject
    lateinit var playerService: PlayerService

    private val scheduleUseCase = EventsDataUseCase()

    init {
        DaggerApiComponent.create().inject(this)
    }

    private val disposable = CompositeDisposable()

    override fun getData() {
        scheduleUseCase.getScheduleData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { data ->
                    val eventsData = data.sortedBy { it.date }
                    view?.hideLoadingView()
                    view?.setItems(eventsData)
                },
                onError = { error ->
                    view?.hideLoadingView()
                    view?.showErrorMessage()
                }
            ).addTo(disposable)
    }

    override fun clearData() {
        this.view = null
        disposable.clear()
    }
}